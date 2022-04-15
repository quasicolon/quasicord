/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.locale;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.locale.translation.PluralTranslation;
import dev.qixils.quasicolon.locale.translation.SingleTranslation;
import dev.qixils.quasicolon.locale.translation.Translation;
import dev.qixils.quasicolon.locale.translation.impl.PluralTranslationImpl;
import dev.qixils.quasicolon.locale.translation.impl.SingleTranslationImpl;
import dev.qixils.quasicolon.locale.translation.impl.UnknownTranslationImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Provides the translation(s) for a key inside the configured namespace.
 * @see TranslationProvider#TranslationProvider(String, Locale)  TranslationProvider constructor
 */
public final class TranslationProvider {
	private static final @NonNull Logger logger = LoggerFactory.getLogger(TranslationProvider.class);
	private final @NonNull String namespace;
	private final @NonNull Locale defaultLocale;
	private final @NonNull Map<Locale, Map<String, ?>> translations = new HashMap<>(1);

	/**
	 * Creates a new translation provider for the given resource source and default locale.
	 * <p>
	 * Your bot or plugin should store its language files inside the directory
	 * {@code src/main/resources/langs/&lt;namespace&gt;}, where {@code &lt;namespace&gt;} is the
	 * same as the string you pass in to the {@code namespace} parameter.
	 * </p>
	 * <b>Note:</b> The {@code namespace} parameter is converted to lowercase. Usage of
	 * non-alphanumeric characters is discouraged, though not explicitly forbidden.
	 *
	 * @param namespace     the directory in which the translations are stored
	 * @param defaultLocale the default locale to use if no translation is found for the current locale
	 */
	public TranslationProvider(@NonNull String namespace, @NonNull Locale defaultLocale) {
		this.namespace = namespace.toLowerCase(Locale.ROOT);
		this.defaultLocale = defaultLocale;
	}

	/**
	 * Gets the namespace of this translation provider.
	 *
	 * @return the namespace
	 */
	public @NonNull String getNamespace() {
		return namespace;
	}

	private @NonNull Translation getTranslation(@NonNull String key, @NonNull Locale locale, @NonNull Locale requestedLocale) {
		if (!translations.containsKey(locale)) {
			// load the translations for the given locale
			Yaml yaml = new Yaml();
			String languageCode = locale.getLanguage().toLowerCase(Locale.ROOT); // TODO: try variants as well (i.e. en_US)
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("langs/" + namespace + '/' + languageCode + ".yaml"); // TODO: test this

			if (inputStream == null) {
				logger.warn("No translation file for locale " + languageCode + " found");
				translations.put(locale, Collections.emptyMap());
			} else {
				// load the translations
				Map<String, Object> translationMap = yaml.load(inputStream);
				// some language files are nested inside the language code, so we need to extract
				// the inner map
				// TODO: this should probably be dumber (i.e. always look for a nested map if the
				//    outer map's size is 1 instead of just trying to grab the inner map via
				//    languageCode)
				if (translationMap.containsKey(languageCode))
					translationMap = (Map<String, Object>) translationMap.get(languageCode);
				// add the translation map to the cache
				translations.put(locale, translationMap);
			}
		}

		// get the translation
		Map<String, ?> localeTranslations = translations.get(locale);
		if (!localeTranslations.containsKey(key)) {
			// key does not exist in requested locale
			if (locale.equals(defaultLocale))
				// key does not exist in default locale either; return unknown translation
				return new UnknownTranslationImpl(key, locale, requestedLocale);

			return getTranslation(key, defaultLocale);
		}

		Object translation = localeTranslations.get(key);
		if (translation instanceof String)
			return new SingleTranslationImpl(key, locale, requestedLocale, (String) translation);
		else if (translation instanceof Map) {
			//noinspection unchecked
			Map<String, String> stringMap = (Map<String, String>) translation;
			return PluralTranslationImpl.fromStringMap(key, locale, requestedLocale, stringMap);
		} else {
			throw new IllegalStateException("Translation for key " + key + " in " + locale + " is not a string or map");
		}
	}

	private @NonNull Translation getTranslation(@NonNull String key, @NonNull Locale locale) {
		return getTranslation(key, locale, locale);
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
		if (translation instanceof SingleTranslation)
			return (SingleTranslation) translation;
		throw new IllegalArgumentException("Translation for key " + key + " is not a string");
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
		if (translation instanceof PluralTranslation)
			return (PluralTranslation) translation;
		throw new IllegalArgumentException("Translation for key " + key + " is not a plural map");
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
