package dev.qixils.quasicolon.locale;

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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Provides the translation for a given key.
 */
public final class TranslationProvider {
	private static final @NonNull Logger logger = LoggerFactory.getLogger(TranslationProvider.class);
	private final @NonNull Class<?> resourceSource;
	private final @NonNull Locale defaultLocale;
	private final @NonNull Map<Locale, Map<String, ?>> translations = new HashMap<>(1);

	/**
	 * Creates a new translation provider given the class whose module will be used as the source
	 * for loading translations. This is generally the main class of your bot or library.
	 *
	 * @param resourceSource the class whose module will be used as the source for loading
	 *                       translations
	 */
	public TranslationProvider(@NonNull Class<?> resourceSource, @NonNull Locale defaultLocale) {
		this.resourceSource = resourceSource;
		this.defaultLocale = defaultLocale;
	}

	private @NonNull Translation getTranslation(@NonNull String key, @NonNull Locale locale, @NonNull Locale requestedLocale) {
		if (!translations.containsKey(locale)) {
			// load the translations for the given locale
			Yaml yaml = new Yaml();
			String languageCode = locale.getLanguage().toLowerCase(Locale.ENGLISH); // TODO: try variants as well (i.e. en_US)
			InputStream inputStream = resourceSource.getResourceAsStream("langs/" + languageCode + ".yaml");

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

	/**
	 * A type of translation provider.
	 */
	public enum Type {
		/**
		 * Indicates that a translation is expected to come from the quasicolon library.
		 */
		LIBRARY,
		/**
		 * Indicates that a translation is expected to come from a bot building upon this library.
		 */
		BOT,
		/**
		 * Indicates that a translation is expected to come from an unknown third party.
		 */
		OTHER;

		public @NonNull TranslationProvider getInstance() {
			return TranslationProvider.getInstance(this);
		}
	}

	private static final @NonNull Map<Type, TranslationProvider> INSTANCES = new EnumMap<>(Type.class);

	/**
	 * Gets the translation provider for the given type.
	 *
	 * @param type the type of translation provider
	 * @return the translation provider
	 */
	public static @NonNull TranslationProvider getInstance(@NonNull Type type) {
		return Objects.requireNonNull(INSTANCES.get(type), () -> "No translation provider registered for type " + type);
	}

	/**
	 * Registers the translation provider for the given type.
	 *
	 * @param type     the type of translation provider
	 * @param provider the translation provider
	 * @throws IllegalArgumentException if a translation provider has already been registered for the given type
	 */
	public static void registerInstance(@NonNull Type type, @NonNull TranslationProvider provider) throws IllegalArgumentException {
		if (INSTANCES.containsKey(type))
			throw new IllegalArgumentException("Translation provider already registered for type " + type);
		INSTANCES.put(type, provider);
	}
}
