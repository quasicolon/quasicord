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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Provides the translation(s) for a key inside the configured namespace.
 * @see LocalizationFunction
 */
public final class TranslationProvider {
	private static final @NonNull Logger logger = LoggerFactory.getLogger(TranslationProvider.class);
	private final @NonNull String namespace;
	private final @NonNull Locale defaultLocale;
	private final @NonNull Set<Locale> locales;
	private final @NonNull Map<String, Map<Locale, ?>> allTranslations = new HashMap<>();
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
	 * @param supportedLocales the locales to load
	 */
	public TranslationProvider(@NonNull String namespace, @NonNull Locale defaultLocale, @NonNull Collection<Locale> supportedLocales) throws IOException {
		this.namespace = namespace.toLowerCase(Locale.ROOT);
		this.defaultLocale = defaultLocale;
		this.locales = new HashSet<>(supportedLocales);
		loadTranslations();
	}

	/**
	 * Creates a new translation provider for the given resource source and default locale.
	 * <p>
	 * Your bot or plugin should store its language files inside the directory
	 * {@code src/main/resources/langs/<namespace>}, where {@code <namespace>} is the
	 * same as the string you pass in to the {@code namespace} parameter.
	 * </p>
	 * <b>Note:</b> The {@code namespace} parameter is converted to lowercase. Usage of
	 * non-alphanumeric characters is discouraged, though not explicitly forbidden.
	 *
	 * @param namespace        the directory in which the translations are stored
	 * @param supportedLocales the locales to load, with the first element being treated as the default locale
	 */
	public TranslationProvider(@NonNull String namespace, @NonNull List<Locale> supportedLocales) throws IOException {
		this(namespace, supportedLocales.get(0), supportedLocales);
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

	/**
	 * Loads translations from the configured namespace.
	 */
	@SuppressWarnings("unchecked")
	public void loadTranslations() throws IOException {
		// reset maps
		allTranslations.clear();
		discordTranslations.clear();
		Yaml yaml = new Yaml();

		for (Locale locale : locales) {
			String languageTag = locale.toLanguageTag();
			DiscordLocale discordLocale = getDiscordLocale(locale);

			// load file
			try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("langs/" + namespace + '/' + languageTag + ".yaml")) {
				if (inputStream == null) {
					logger.warn("Failed to load translations for locale {} in namespace {}: file not found", locale, namespace);
					continue;
				}

				Map<String, Object> translationMap = yaml.load(inputStream);
				if (translationMap == null) {
					logger.warn("Failed to load translations for locale {} in namespace {}: yaml returned null", locale, namespace);
					continue;
				}

				// some language files are nested inside the language code, so we need to extract
				// the inner map
				if (translationMap.containsKey(languageTag))
					translationMap = (Map<String, Object>) translationMap.get(languageTag);

				// add translations to maps
				for (Map.Entry<String, Object> entry : translationMap.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					// type-checking here feels kinda redundant, but I suppose it's good
					//  to warn developers of improper i18n files earlier rather than later
					if (value instanceof String translation) {
						// single translation
						Map<Locale, String> translations = (Map<Locale, String>) this.allTranslations.computeIfAbsent(key, k -> new HashMap<>());
						translations.put(locale, translation);
						if (discordLocale == DiscordLocale.UNKNOWN)
							continue;
						Map<DiscordLocale, String> discordTranslations = this.discordTranslations.computeIfAbsent(key, k -> new HashMap<>());
						discordTranslations.put(discordLocale, translation);
					} else if (value instanceof Map) {
						// plural translation
						Map<Locale, Map<String, String>> translations = (Map<Locale, Map<String, String>>) allTranslations.computeIfAbsent(key, k -> new HashMap<>());
						translations.put(locale, (Map<String, String>) value);
						// (discord doesn't support plural translations)
					} else {
						logger.warn("Invalid translation value for key '{}' in {} file {}: {}", key, namespace, languageTag, value);
					}
				}

				// log
				logger.info("Loaded {} translations for locale {} in namespace {}", translationMap.size(), locale, namespace);
			}
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
	@NotNull
	public Map<DiscordLocale, String> getDiscordTranslations(@NotNull String key) {
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
