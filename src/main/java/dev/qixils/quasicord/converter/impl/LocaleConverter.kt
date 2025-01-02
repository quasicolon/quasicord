/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.converter.impl

import dev.qixils.quasicord.Key.Companion.library
import dev.qixils.quasicord.Quasicord
import dev.qixils.quasicord.converter.Converter
import dev.qixils.quasicord.error.UserError
import net.dv8tion.jda.api.interactions.Interaction
import java.util.*

/**
 * Attempts to find the Locale matching a user's input.
 * It does so by searching language tags,
 * names of languages in their detected language,
 * and names of languages in English.
 */
class LocaleConverter(private val library: Quasicord) : Converter<String, Locale> {
    override val inputClass: Class<String> = String::class.java
	override val outputClass: Class<Locale> = Locale::class.java

    override fun convert(it: Interaction, input: String, targetClass: Class<out Locale>): Locale {
        try {
            return Locale.Builder().setLanguageTag(input).build()
        } catch (_: IllformedLocaleException) {
            val userLocale = library.localeProvider.forInteraction(it).block() // TODO: async?? :(
            if (userLocale != null) {
                val localLowVal = input.lowercase(userLocale)
                val localMatch = Locale.availableLocales()
                    .filter { l: Locale? -> l!!.getDisplayName(userLocale).lowercase(userLocale) == localLowVal }
                    .findFirst()
                if (localMatch.isPresent) return localMatch.get()
            }

            val engLowVal = input.lowercase()
            val engMatch = Locale.availableLocales()
                .filter { l: Locale? -> l!!.getDisplayName(Locale.ENGLISH).lowercase() == engLowVal }
                .findFirst()
            if (engMatch.isPresent) return engMatch.get()

            throw UserError(library("exception.invalid_locale"), input)
        }
    }
}
