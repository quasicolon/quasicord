/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord

import dev.qixils.quasicord.locale.TranslationProvider
import dev.qixils.quasicord.locale.translation.PluralTranslation
import dev.qixils.quasicord.locale.translation.SingleTranslation
import org.jetbrains.annotations.ApiStatus
import java.util.*

/**
 * A representation of a translation key.
 *
 * This includes the usual [string value][.value] as well as the
 * [namespace][.namespace] in which the translation is defined.
 *
 * The namespace always corresponds to a registered
 * [TranslationProvider][TranslationProvider].
 * For convenience, this <!-- TODO: I've just discovered this incomplete sentence and I've no idea what it's meant to say. -->
 */
data class Key @ApiStatus.Internal constructor(
	val namespace: String,
	val value: String,
) {
    // helpers
    /**
     * Get the translation provider for this key.
     *
     * @return translation provider
     * @throws IllegalStateException if no translation provider is registered for the
     * stored [namespace][.namespace]
     */
    @Throws(IllegalStateException::class)
    fun translationProvider(): TranslationProvider {
        return TranslationProvider.getInstance(this)
    }

    /**
     * Gets this key's single translation (i.e. non-plural) for the given locale.
     *
     * @param locale the locale to get the translation for
     * @return the translation
     * @throws IllegalArgumentException if the translation is not a single translation
     * @throws IllegalStateException    if no translation provider is registered for the
     * stored [namespace][.namespace]
     */
    fun getSingle(locale: Locale): SingleTranslation {
        return translationProvider().getSingle(value, locale)
    }

    /**
     * Gets this key's plural translation for the given locale.
     *
     * @param locale the locale to get the translation for
     * @return the translation
     * @throws IllegalArgumentException if the translation is not a plural translation
     * @throws IllegalStateException    if no translation provider is registered for the
     * stored [namespace][.namespace]
     */
    fun getPlural(locale: Locale): PluralTranslation {
        return translationProvider().getPlural(value, locale)
    }

    companion object {
        // static constructors
        /**
         * The namespace internally used by the quasicord library.
         */
        @ApiStatus.Internal
        const val LIBRARY_NAMESPACE: String = "quasicord"

        /**
         * Constructs a new internal library key from the given value.
         *
         * @param value the translation key
         * @return a new [Key]
         */
        @JvmStatic
		@ApiStatus.Internal
        fun library(value: String): Key {
            return key(LIBRARY_NAMESPACE, value)
        }

        /**
         * Constructs a new key from the given namespace and value.
         * All parameters are case-insensitive.
         *
         * @param namespace the namespace
         * @param value     the translation key
         * @return a new [Key]
         */
        fun key(namespace: String, value: String): Key {
            return Key(namespace.lowercase(), value.lowercase())
        }
    }
}
