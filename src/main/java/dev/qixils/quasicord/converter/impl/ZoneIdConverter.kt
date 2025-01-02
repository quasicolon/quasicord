/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.converter.impl

import dev.qixils.quasicord.Key.Companion.library
import dev.qixils.quasicord.converter.Converter
import dev.qixils.quasicord.error.UserError
import net.dv8tion.jda.api.interactions.Interaction
import java.time.ZoneId

/**
 * Attempts to find the timezone matching a user's input.
 * It does so by searching timezone IDs,
 * names of timezones in the user's detected language,
 * and names of timezones in English.
 */
class ZoneIdConverter : Converter<String, ZoneId> {
    override val inputClass: Class<String> = String::class.java
	override val outputClass: Class<ZoneId> = ZoneId::class.java

    override fun convert(it: Interaction, input: String, targetClass: Class<out ZoneId>): ZoneId {
        try {
            return ZoneId.of(input)
        } catch (_: Exception) {
            throw UserError(library("exception.invalid_timezone"), input)
        }
    }
}
