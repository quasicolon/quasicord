/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.autocomplete.impl

import dev.qixils.quasicord.CommandManager
import dev.qixils.quasicord.Key.Companion.library
import dev.qixils.quasicord.autocomplete.AutoCompleter
import dev.qixils.quasicord.locale.Context
import dev.qixils.quasicord.text.Text.Companion.single
import net.dv8tion.jda.api.interactions.callbacks.IAutoCompleteCallback
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

/**
 * Auto-completes a regional timezone.
 */
class TimeZoneAutoCompleter(protected val commandManager: CommandManager) : AutoCompleter {
    override suspend fun getSuggestions(event: IAutoCompleteCallback): List<Command.Choice> {
        if (event !is CommandAutoCompleteInteraction) return emptyList()
		val locale = commandManager.library
			.localeProvider
			.forContext(Context.fromInteraction(event))
		val input = event.focusedOption.value.lowercase(locale)
		return ZoneId.getAvailableZoneIds()
			.map { zoneId -> ZoneId.of(zoneId) }
			.filter {
				val compare = format(it, locale).lowercase(locale)
				compare.contains(input) || compare.replace("[/_]".toRegex(), " ").contains(input)
			}
			.sortedBy { format(it, locale) }
			.take(OptionData.MAX_CHOICES)
			.map { Command.Choice(format(it, locale), it.id) }
    }

    companion object {
        protected fun format(zone: ZoneId, locale: Locale): String {
            return single(
                library("timezone_display"),
                zone.getDisplayName(TextStyle.FULL_STANDALONE, locale),
                zone.id
            ).asString(locale)
        }
    }
}
