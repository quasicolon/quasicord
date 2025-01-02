/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale

import dev.qixils.quasicord.Key
import dev.qixils.quasicord.locale.translation.PluralTranslation
import dev.qixils.quasicord.locale.translation.SingleTranslation
import dev.qixils.quasicord.locale.translation.Translation
import dev.qixils.quasicord.locale.translation.UnknownTranslation
import dev.qixils.quasicord.locale.translation.impl.PluralTranslationImpl
import dev.qixils.quasicord.locale.translation.impl.SingleTranslationImpl
import dev.qixils.quasicord.locale.translation.impl.UnknownTranslationImpl
import net.dv8tion.jda.api.interactions.DiscordLocale
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.IOException
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.Throws

/**
 * Provides the translation(s) for a key inside the configured namespace.
 * @see net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction
 */
class TranslationProvider(
    namespace: String,
    /**
     * Gets the default locale of this translation provider.
     *
     * @return the default locale
     */
	val defaultLocale: Locale
) {
    /**
     * Gets the namespace of this translation provider.
     *
     * @return the namespace
     */
	val namespace: String = namespace.lowercase()

    private val locales: MutableSet<Locale> = HashSet<Locale>()
    private val allTranslations: MutableMap<String, MutableMap<Locale, Any>> =
        HashMap<String, MutableMap<Locale, Any>>()
    private val discordTranslations: MutableMap<String, MutableMap<DiscordLocale, String>> =
        HashMap<String, MutableMap<DiscordLocale, String>>()

	/**
	 * Creates a new translation provider for the given resource source and default locale.
	 *
	 *
	 * Your bot or plugin should store its language files inside the directory
	 * `src/main/resources/langs/<namespace>`, where `<namespace>` is the
	 * same as the string you pass in to the `namespace` parameter.
	 * The names of the files should end in `.yaml`, i.e. `en_US.yaml`.
	 *
	 * **Note:** The `namespace` parameter is converted to lowercase. Usage of
	 * non-alphanumeric characters is discouraged, though not explicitly forbidden.
	 *
	 * @param namespace        the directory in which the translations are stored
	 * @param defaultLocale    the default locale to use if no translation is found for the current locale
	 */
	init {
		loadTranslations()
	}

    /**
     * Returns a view of the supported locales.
     *
     * @return locale view
     */
	val localesView: Set<Locale> get() = Collections.unmodifiableSet(locales)

    /**
     * Fetches a list of alternative locales for the given locale with the default locale included.
     *
     * @param locale the locale to fetch alternatives for
     * @return a list of alternative locales
     */
    private fun getAlternativesWithDefault(locale: Locale): MutableList<Locale> {
        val alternatives: MutableList<Locale> = getAlternatives(locale)
        alternatives.add(defaultLocale)
        return alternatives
    }

    // Gets all the translations contained in a (maybe nested) yaml value
    private fun flattenKeys(prefix: String?, keys: Map<String, *>): Map<String, Any?> {
        val results = mutableMapOf<String, Any?>()
        for (entry in keys.entries) {
            val key = prefix + entry.key
			val value = entry.value
            when (value) {
                is String -> results.put(key, value)
                is Map<*, *> ->
					if (PLURAL_KEYS.containsAll(value.keys)) results.put(key, value)
                	else results.putAll(flattenKeys("$key.", value as Map<String, *>))
                else -> throw RuntimeException("Invalid translation value [$key: $value]")
            }
        }
        return results
    }

    // Find all the items in the JVM resource path
    @Throws(IOException::class)
    private fun listResourcesIn(path: String): List<String> {
        val url = ClassLoader.getSystemResource(path)
        if (url.protocol == "file") { // OS dir
            return File(url.path).list().toList()
        } else { // packed in jar
            JarFile(url.path.substring(5, url.path.indexOf("!"))).use { jar ->
                return Collections.list(jar.entries())
                    .filter { entry: JarEntry? -> entry!!.getName().startsWith(path) }
                    .map { entry: JarEntry? -> entry!!.getName().substring(path.length) }
                    .toList()
            }
        }
    }

    /**
     * Loads translations from the configured namespace.
     */
    @Throws(IOException::class)
    private fun loadTranslations() {
        val yaml = Yaml()

        // each language yaml file
        for (filename in listResourcesIn("langs/$namespace/")) {
            val matcher: Matcher = LANGUAGE_FILE.matcher(filename)
            if (!matcher.find()) continue

            // tag from filename (e.g. en)
            val languageTag = matcher.group("languageTag")
            val locale = Locale.forLanguageTag(languageTag)
            locales.add(locale!!)
            val discordLocale: DiscordLocale = getDiscordLocale(locale)

            val file = ClassLoader.getSystemResourceAsStream("langs/$namespace/$filename")
            var data = yaml.load<Map<String, Any?>>(file)

            // some language files are nested inside the language tag
            if (data.containsKey(languageTag)) data = data[languageTag] as MutableMap<String, Any?>
            // convert to flat keys for dotted string access
            data = flattenKeys("", data)

            for (entry in data.entries) {
                val key: String? = entry.key
                val value: Any? = entry.value

                allTranslations.computeIfAbsent(key!!) { mutableMapOf<Locale, Any>() }.put(locale, value!!)

                if (discordLocale != DiscordLocale.UNKNOWN // not all locales are Discord-supported
                    && value is String
                )  // only singular translations
                    discordTranslations.computeIfAbsent(
                        key,
                        { mutableMapOf<DiscordLocale, String>() }).put(discordLocale, value)
            }

            logger.info("Loaded {} translations for locale {} in namespace {}", data.size, locale, namespace)
        }
    }

    private fun tryGetTranslation(key: String, locale: Locale, requestedLocale: Locale): Translation? {
        // get the translation map
        // (null check was performed in caller)
        val translations = allTranslations[key] ?: return null

        // check if there is a translation for the given locale
        if (!translations.containsKey(locale)) return null

        // parse the translation
        val translation: Any? = translations[locale]
        if (translation is String) return SingleTranslationImpl(key, locale, requestedLocale, translation)

        if (translation is MutableMap<*, *>) {
            val stringMap = translation as MutableMap<String?, String?>
            return PluralTranslationImpl.fromStringMap(key, locale, requestedLocale, stringMap)
        }

        // this should never happen, but just in case
        logger.warn("Invalid translation value for key '{}' in {} locale '{}': {}", key, namespace, locale, translation)
        return null
    }

    private fun getTranslation(key: String, locale: Locale): Translation {
        // ensure the key is valid
        if (!allTranslations.containsKey(key)) return UnknownTranslationImpl(key, locale)

        // search for a translation
        for (variant in getAlternativesWithDefault(locale)) {
            val translation = tryGetTranslation(key, variant, locale)
            if (translation != null) return translation
        }

        // no translation found
        return UnknownTranslationImpl(key, locale)
    }

    /**
     * Gets the single translation (i.e. non-plural) for the given key and locale.
     *
     * @param key    the translation key
     * @param locale the locale to get the translation for
     * @return the translation
     * @throws IllegalArgumentException if the translation is not a single translation
     */
    @Throws(IllegalArgumentException::class)
    fun getSingle(key: String, locale: Locale): SingleTranslation {
        val translation: Any = getTranslation(key, locale)
        if (translation is SingleTranslation) return translation
        throw IllegalArgumentException("Translation for key $key is not a string")
    }

    /**
     * Gets the single translation (i.e. non-plural) for the given key and default locale.
     *
     * @param key    the translation key
     * @return the translation
     * @throws IllegalArgumentException if the translation is not a single translation
     */
    @Throws(IllegalArgumentException::class)
    fun getSingleDefault(key: String): SingleTranslation {
        return getSingle(key, this.defaultLocale)
    }

    /**
     * Gets the single translation (i.e. non-plural) for the given key and locale, or throws if not present.
     *
     * @param key    the translation key
     * @param locale the locale to get the translation for
     * @return the translation
     * @throws IllegalArgumentException if the translation is not a single translation or is not present
     */
    @Throws(IllegalArgumentException::class)
    fun getSingleOrThrow(key: String, locale: Locale): SingleTranslation {
        val translation = getSingle(key, locale)
        check(translation !is UnknownTranslation) { "Missing $locale translation for $namespace:$key" }
        return translation
    }

    /**
     * Gets the single translation (i.e. non-plural) for the given key and default locale, or throws if not present.
     *
     * @param key    the translation key
     * @return the translation
     * @throws IllegalArgumentException if the translation is not a single translation or is not present
     */
    @Throws(IllegalArgumentException::class)
    fun getSingleDefaultOrThrow(key: String): SingleTranslation {
        return getSingleOrThrow(key, this.defaultLocale)
    }

    /**
     * Gets the plural translation for the given key and locale.
     *
     * @param key    the translation key
     * @param locale the locale to get the translation for
     * @return the translation
     * @throws IllegalArgumentException if the translation is not a plural translation
     */
    @Throws(IllegalArgumentException::class)
    fun getPlural(key: String, locale: Locale): PluralTranslation {
        val translation: Any = getTranslation(key, locale)
        if (translation is PluralTranslation) return translation
        throw IllegalArgumentException("Translation for key $key is not a plural map")
    }

    /**
     * Gets the plural translation for the given key and default locale.
     *
     * @param key    the translation key
     * @return the translation
     * @throws IllegalArgumentException if the translation is not a plural translation
     */
    @Throws(IllegalArgumentException::class)
    fun getPluralDefault(key: String): PluralTranslation {
        return getPlural(key, this.defaultLocale)
    }

    /**
     * Gets the plural translation for the given key and locale, or throws if not present.
     *
     * @param key    the translation key
     * @param locale the locale to get the translation for
     * @return the translation
     * @throws IllegalArgumentException if the translation is not a single translation or is not present
     */
    @Throws(IllegalArgumentException::class)
    fun getPluralOrThrow(key: String, locale: Locale): PluralTranslation {
        val translation = getPlural(key, locale)
        check(translation !is UnknownTranslation) { "Missing $locale translation for $namespace:$key" }
        return translation
    }

    /**
     * Gets the plural translation for the given key and default locale, or throws if not present.
     *
     * @param key    the translation key
     * @return the translation
     * @throws IllegalArgumentException if the translation is not a single translation or is not present
     */
    @Throws(IllegalArgumentException::class)
    fun getPluralDefaultOrThrow(key: String): PluralTranslation {
        return getPluralOrThrow(key, this.defaultLocale)
    }

    /**
     * Gets the Discord translation map for the given key.
     *
     * @param key the translation key
     * @return the translation map
     */
    fun getDiscordTranslations(key: String): Map<DiscordLocale, String> {
        return discordTranslations.getOrDefault(key, emptyMap())
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TranslationProvider::class.java)

        /**
         * Fetches a list of alternative locales for the given locale.
         *
         * @param locale the locale to fetch alternatives for
         * @return a list of alternative locales
         */
        private fun getAlternatives(locale: Locale): MutableList<Locale> {
            // ResourceBundle.Control has a great algorithm for searching for the best locale which for some reason is
            //  nearly impossible to use! but I figured it out!
            //  fun fact: the message accepts a string parameter and does a null check on it but doesn't actually use it lol
            val candidates: MutableList<Locale> = ArrayList<Locale>(
                ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", locale)
            )
            candidates.removeAt(candidates.size - 1) // remove the ROOT locale
            return candidates
        }

        /**
         * Fetches a [DiscordLocale] equivalent to the given [Locale].
         *
         * @param locale the locale to convert
         * @return a Discord locale
         */
        private fun getDiscordLocale(locale: Locale): DiscordLocale {
            for (variant in getAlternatives(locale)) {
                val discordLocale = DiscordLocale.from(variant)
                if (discordLocale != DiscordLocale.UNKNOWN) return discordLocale
            }
            return DiscordLocale.UNKNOWN
        }

        private val PLURAL_KEYS: Set<String> = setOf("zero", "one", "two", "few", "many", "other")

        private val LANGUAGE_FILE: Pattern = Pattern.compile("(?<languageTag>\\w{2})\\.ya?ml")

        // static instance management
        private val INSTANCES: MutableMap<String, TranslationProvider> = HashMap(2)

        /**
         * Gets the registered translation provider for the provided namespace.
         *
         *
         * Note that the namespace is case-insensitive.
         *
         * @param namespace the namespace to get the translation provider for
         * @return the translation provider
         * @throws IllegalStateException if no translation provider is registered for the given namespace
         */
        @JvmStatic
		@Throws(IllegalStateException::class)
        fun getInstance(namespace: String): TranslationProvider {
            val namespace = namespace.lowercase(Locale.ROOT)
            check(INSTANCES.containsKey(namespace)) { "No translation provider registered for namespace $namespace" }
            return INSTANCES[namespace]!!
        }

        /**
         * Gets the registered translation provider for the provided key's namespace.
         *
         * @param key the key to get the translation provider for
         * @return the translation provider
         * @throws IllegalStateException if no translation provider is registered for the key's namespace
         */
        @Throws(IllegalStateException::class)
        fun getInstance(key: Key): TranslationProvider {
            return getInstance(key.namespace)
        }

        /**
         * Registers the provided translation provider with its associated namespace.
         *
         * @param provider the translation provider
         * @throws IllegalStateException if a translation provider has already been registered for the given type
         */
        @Throws(IllegalArgumentException::class)
        fun registerInstance(provider: TranslationProvider) {
            val namespace = provider.namespace.lowercase(Locale.ROOT)
            check(!INSTANCES.containsKey(namespace)) { "Translation provider already registered for namespace $namespace" }
            INSTANCES.put(namespace, provider)
        }
    }
}
