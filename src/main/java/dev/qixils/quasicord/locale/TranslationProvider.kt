/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.locale;

import dev.qixils.quasicord.Key;
import dev.qixils.quasicord.locale.translation.PluralTranslation;
import dev.qixils.quasicord.locale.translation.SingleTranslation;
import dev.qixils.quasicord.locale.translation.Translation;
import dev.qixils.quasicord.locale.translation.UnknownTranslation;
import dev.qixils.quasicord.locale.translation.impl.PluralTranslationImpl;
import dev.qixils.quasicord.locale.translation.impl.SingleTranslationImpl;
import dev.qixils.quasicord.locale.translation.impl.UnknownTranslationImpl;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * Provides the translation(s) for a key inside the configured namespace.
 * @see LocalizationFunction
 */
public final class TranslationProvider {
	private static final @NonNull Logger logger = LoggerFactory.getLogger(TranslationProvider.class);
	private final @NonNull String namespace;
	private final @NonNull Locale defaultLocale;
	private final @NonNull Set<Locale> locales = new HashSet<>();
	private final @NonNull Map<String, Map<Locale, Object>> allTranslations = new HashMap<>();
	private final @NonNull Map<String, Map<DiscordLocale, String>> discordTranslations = new HashMap<>();

	/**
	 * Creates a new translation provider for the given resource source and default locale.
	 * <p>
	 * Your bot or plugin should store its language files inside the directory
	 * {@code src/main/resources/langs/<namespace>}, where {@code <namespace>} is the
	 * same as the string you pass in to the {@code namespace} parameter.
	 * The names of the files should end in {@code .yaml}, i.e. {@code en_US.yaml}.
	 * </p>
	 * <b>Note:</b> The {@code namespace} parameter is converted to lowercase. Usage of
	 * non-alphanumeric characters is discouraged, though not explicitly forbidden.
	 *
	 * @param namespace        the directory in which the translations are stored
	 * @param defaultLocale    the default locale to use if no translation is found for the current locale
	 */
	public TranslationProvider(@NonNull String namespace, @NonNull Locale defaultLocale) throws IOException {
		this.namespace = namespace.toLowerCase(Locale.ROOT);
		this.defaultLocale = defaultLocale;
		loadTranslations();
	}

	/**
	 * Returns a view of the supported locales.
	 *
	 * @return locale view
	 */
	public Set<Locale> getLocales() {
		return Collections.unmodifiableSet(locales);
	}

	/**
	 * Fetches a list of alternative locales for the given locale.
	 *
	 * @param locale the locale to fetch alternatives for
	 * @return a list of alternative locales
	 */
	private static @NonNull List<Locale> getAlternatives(@NonNull Locale locale) {
		// ResourceBundle.Control has a great algorithm for searching for the best locale which for some reason is
		//  nearly impossible to use! but I figured it out!
		//  fun fact: the message accepts a string parameter and does a null check on it but doesn't actually use it lol
		List<Locale> candidates = new ArrayList<>(ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", locale));
		candidates.remove(candidates.size() - 1); // remove the ROOT locale
		return candidates;
	}

	/**
	 * Fetches a list of alternative locales for the given locale with the default locale included.
	 *
	 * @param locale the locale to fetch alternatives for
	 * @return a list of alternative locales
	 */
	private @NonNull List<Locale> getAlternativesWithDefault(@NonNull Locale locale) {
		List<Locale> alternatives = getAlternatives(locale);
		alternatives.add(defaultLocale);
		return alternatives;
	}

	/**
	 * Fetches a {@link DiscordLocale} equivalent to the given {@link Locale}.
	 *
	 * @param locale the locale to convert
	 * @return a Discord locale
	 */
	private static @NonNull DiscordLocale getDiscordLocale(@NonNull Locale locale) {
		for (Locale variant : getAlternatives(locale)) {
			DiscordLocale discordLocale = DiscordLocale.from(variant);
			if (discordLocale != DiscordLocale.UNKNOWN)
				return discordLocale;
		}
		return DiscordLocale.UNKNOWN;
	}

	private static final Set<String> PLURAL_KEYS = new HashSet<>(List.of("zero", "one", "two", "few", "many", "other"));

	// Gets all the translations contained in a (maybe nested) yaml value
	@SuppressWarnings("unchecked")
	private HashMap<String, Object> flattenKeys(String prefix, Map<String, ?> keys) {
		var results = new HashMap<String, Object>();
		for (var entry : keys.entrySet()) {
			var key = prefix + entry.getKey();
			switch (entry.getValue()) {
				case String single ->
					results.put(key, single);
				case Map<?, ?> map when PLURAL_KEYS.containsAll((Set<String>)map.keySet()) ->
					results.put(key, map);
				case Map<?, ?> map ->
					results.putAll(flattenKeys(key + ".", (Map<String, ?>) map));
				case Object other ->
					throw new RuntimeException("Invalid translation value: " + key + ": " + other);
			}
		}
		return results;
	}

	private static final Pattern LANGUAGE_FILE = Pattern.compile("(?<languageTag>\\w{2})\\.ya?ml");

	// Find all the items in the JVM resource path
	private List<String> listResourcesIn(String path) throws IOException {
		var url = ClassLoader.getSystemResource(path);
		if (url.getProtocol().equals("file")) { // OS dir
			return Arrays.stream(new File(url.getPath()).list()).toList();
		} else { // packed in jar
			try (var jar = new JarFile(url.getPath().substring(5, url.getPath().indexOf("!")))) {
				return Collections.list(jar.entries()).stream()
					.filter(entry -> entry.getName().startsWith(path)         )
					.map(   entry -> entry.getName().substring(path.length()) )
					.toList();
			}
		}
	}

	/**
	 * Loads translations from the configured namespace.
	 */
	@SuppressWarnings("unchecked")
	private void loadTranslations() throws IOException {
		Yaml yaml = new Yaml();

		// each language yaml file
		for (var filename : listResourcesIn("langs/" + namespace + "/")) {
			var matcher = LANGUAGE_FILE.matcher(filename);
			if (!matcher.find()) continue;

			// tag from filename (e.g. en)
			var languageTag = matcher.group("languageTag");
			var locale = Locale.forLanguageTag(languageTag);
			locales.add(locale);
			var discordLocale = getDiscordLocale(locale);

			var file = ClassLoader.getSystemResourceAsStream("langs/" + namespace + "/" + filename);
			Map<String, Object> data = yaml.load(file);

			// some language files are nested inside the language tag
			if (data.containsKey(languageTag))
				data = (Map<String, Object>) data.get(languageTag);
			// convert to flat keys for dotted string access
			data = flattenKeys("", data);

			for (var entry : data.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();

				allTranslations.computeIfAbsent(key, k -> new HashMap<>()).put(locale, value);

				if (discordLocale != DiscordLocale.UNKNOWN // not all locales are Discord-supported
					&& value instanceof String single)     // only singular translations
					discordTranslations.computeIfAbsent(key, k -> new HashMap<>()).put(discordLocale, single);
			}

			logger.info("Loaded {} translations for locale {} in namespace {}", data.size(), locale, namespace);
		}
	}

	/**
	 * Gets the namespace of this translation provider.
	 *
	 * @return the namespace
	 */
	public @NonNull String getNamespace() {
		return namespace;
	}

	/**
	 * Gets the default locale of this translation provider.
	 *
	 * @return the default locale
	 */
	public @NonNull Locale getDefaultLocale() {
		return defaultLocale;
	}

	private @Nullable Translation tryGetTranslation(@NonNull String key, @NonNull Locale locale, @NonNull Locale requestedLocale) {
		// get the translation map
		// (null check was performed in caller)
		Map<Locale, ?> translations = allTranslations.get(key);

		// check if there is a translation for the given locale
		if (!translations.containsKey(locale))
			return null;

		// parse the translation
		Object translation = translations.get(locale);
		if (translation instanceof String value)
			return new SingleTranslationImpl(key, locale, requestedLocale, value);

		if (translation instanceof Map) {
			//noinspection unchecked
			Map<String, String> stringMap = (Map<String, String>) translation;
			return PluralTranslationImpl.fromStringMap(key, locale, requestedLocale, stringMap);
		}

		// this should never happen, but just in case
		logger.warn("Invalid translation value for key '{}' in {} locale '{}': {}", key, namespace, locale, translation);
		return null;
	}

	private @NonNull Translation getTranslation(@NonNull String key, @NonNull Locale locale) {
		// ensure the key is valid
		if (!allTranslations.containsKey(key))
			return new UnknownTranslationImpl(key, locale);

		// search for a translation
		for (Locale variant : getAlternativesWithDefault(locale)) {
			Translation translation = tryGetTranslation(key, variant, locale);
			if (translation != null)
				return translation;
		}

		// no translation found
		return new UnknownTranslationImpl(key, locale);
	}

	/**
	 * Gets the single translation (i.e. non-plural) for the given key and locale.
	 *
	 * @param key    the translation key
	 * @param locale the locale to get the translation for
	 * @return the translation
	 * @throws IllegalArgumentException if the translation is not a single translation
	 */
	public @NonNull SingleTranslation getSingle(@NonNull String key, @NonNull Locale locale) throws IllegalArgumentException {
		Object translation = getTranslation(key, locale);
		if (translation instanceof SingleTranslation value)
			return value;
		throw new IllegalArgumentException("Translation for key " + key + " is not a string");
	}

	/**
	 * Gets the single translation (i.e. non-plural) for the given key and default locale.
	 *
	 * @param key    the translation key
	 * @return the translation
	 * @throws IllegalArgumentException if the translation is not a single translation
	 */
	public @NonNull SingleTranslation getSingleDefault(@NonNull String key) throws IllegalArgumentException {
		return getSingle(key, getDefaultLocale());
	}

	/**
	 * Gets the single translation (i.e. non-plural) for the given key and locale, or throws if not present.
	 *
	 * @param key    the translation key
	 * @param locale the locale to get the translation for
	 * @return the translation
	 * @throws IllegalArgumentException if the translation is not a single translation or is not present
	 */
	public @NonNull SingleTranslation getSingleOrThrow(@NonNull String key, @NonNull Locale locale) throws IllegalArgumentException {
		SingleTranslation translation = getSingle(key, locale);
		if (translation instanceof UnknownTranslation) {
			throw new IllegalStateException("Missing " + locale + " translation for " + namespace + ":" + key);
		}
		return translation;
	}

	/**
	 * Gets the single translation (i.e. non-plural) for the given key and default locale, or throws if not present.
	 *
	 * @param key    the translation key
	 * @return the translation
	 * @throws IllegalArgumentException if the translation is not a single translation or is not present
	 */
	public @NonNull SingleTranslation getSingleDefaultOrThrow(@NonNull String key) throws IllegalArgumentException {
		return getSingleOrThrow(key, getDefaultLocale());
	}

	/**
	 * Gets the plural translation for the given key and locale.
	 *
	 * @param key    the translation key
	 * @param locale the locale to get the translation for
	 * @return the translation
	 * @throws IllegalArgumentException if the translation is not a plural translation
	 */
	public @NonNull PluralTranslation getPlural(@NonNull String key, @NonNull Locale locale) throws IllegalArgumentException {
		Object translation = getTranslation(key, locale);
		if (translation instanceof PluralTranslation value)
			return value;
		throw new IllegalArgumentException("Translation for key " + key + " is not a plural map");
	}

	/**
	 * Gets the plural translation for the given key and default locale.
	 *
	 * @param key    the translation key
	 * @return the translation
	 * @throws IllegalArgumentException if the translation is not a plural translation
	 */
	public @NonNull PluralTranslation getPluralDefault(@NonNull String key) throws IllegalArgumentException {
		return getPlural(key, getDefaultLocale());
	}

	/**
	 * Gets the plural translation for the given key and locale, or throws if not present.
	 *
	 * @param key    the translation key
	 * @param locale the locale to get the translation for
	 * @return the translation
	 * @throws IllegalArgumentException if the translation is not a single translation or is not present
	 */
	public @NonNull PluralTranslation getPluralOrThrow(@NonNull String key, @NonNull Locale locale) throws IllegalArgumentException {
		PluralTranslation translation = getPlural(key, locale);
		if (translation instanceof UnknownTranslation) {
			throw new IllegalStateException("Missing " + locale + " translation for " + namespace + ":" + key);
		}
		return translation;
	}

	/**
	 * Gets the plural translation for the given key and default locale, or throws if not present.
	 *
	 * @param key    the translation key
	 * @return the translation
	 * @throws IllegalArgumentException if the translation is not a single translation or is not present
	 */
	public @NonNull PluralTranslation getPluralDefaultOrThrow(@NonNull String key) throws IllegalArgumentException {
		return getPluralOrThrow(key, getDefaultLocale());
	}

	/**
	 * Gets the Discord translation map for the given key.
	 *
	 * @param key the translation key
	 * @return the translation map
	 */
	@NonNull
	public Map<DiscordLocale, String> getDiscordTranslations(@NonNull String key) {
		return discordTranslations.getOrDefault(key, Collections.emptyMap());
	}

	// static instance management

	private static final @NonNull Map<String, TranslationProvider> INSTANCES = new HashMap<>(2);

	/**
	 * Gets the registered translation provider for the provided namespace.
	 * <p>
	 * Note that the namespace is case-insensitive.
	 *
	 * @param namespace the namespace to get the translation provider for
	 * @return the translation provider
	 * @throws IllegalStateException if no translation provider is registered for the given namespace
	 */
	public static @NonNull TranslationProvider getInstance(@NonNull String namespace) throws IllegalStateException {
		namespace = namespace.toLowerCase(Locale.ROOT);
		if (!INSTANCES.containsKey(namespace))
			throw new IllegalStateException("No translation provider registered for namespace " + namespace);
		return INSTANCES.get(namespace);
	}

	/**
	 * Gets the registered translation provider for the provided key's namespace.
	 *
	 * @param key the key to get the translation provider for
	 * @return the translation provider
	 * @throws IllegalStateException if no translation provider is registered for the key's namespace
	 */
	public static @NonNull TranslationProvider getInstance(@NonNull Key key) throws IllegalStateException {
		return getInstance(key.namespace());
	}

	/**
	 * Registers the provided translation provider with its associated namespace.
	 *
	 * @param provider the translation provider
	 * @throws IllegalStateException if a translation provider has already been registered for the given type
	 */
	public static void registerInstance(@NonNull TranslationProvider provider) throws IllegalArgumentException {
		String namespace = provider.getNamespace();
		if (INSTANCES.containsKey(namespace))
			throw new IllegalStateException("Translation provider already registered for namespace " + namespace);
		INSTANCES.put(namespace, provider);
	}
}
