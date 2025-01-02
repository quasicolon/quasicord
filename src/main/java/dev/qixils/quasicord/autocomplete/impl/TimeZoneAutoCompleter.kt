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
import reactor.core.publisher.Flux
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

/**
 * Auto-completes a regional timezone.
 */
class TimeZoneAutoCompleter(protected val commandManager: CommandManager) : AutoCompleter {
    override fun getSuggestions(event: IAutoCompleteCallback): Flux<Command.Choice> {
        if (event !is CommandAutoCompleteInteraction) return Flux.empty()
        return commandManager.library
            .localeProvider
            .forContext(Context.fromInteraction(event))
            .flatMapMany { locale ->
                val input = event.focusedOption.value.lowercase(locale)
                Flux.fromIterable(ZoneId.getAvailableZoneIds())
                    .map { zoneId -> ZoneId.of(zoneId) }
                    .filter { id ->
                        val compare = format(id, locale).lowercase(locale)
                        compare.contains(input) || compare.replace("[/_]".toRegex(), " ").contains(input)
                    }
                    .sort(Comparator.comparing { id ->
						format(
							id!!,
							locale
						)
                    })
                    .take(OptionData.MAX_CHOICES.toLong())
                    .map { id ->
                        Command.Choice(
							format(id, locale),
                            id.id
                        )
                    }
            }
    }

    companion object {
        protected fun format(zone: ZoneId, locale: Locale): String {
            return single(
                library("timezone_display"),
                zone.getDisplayName(TextStyle.FULL_STANDALONE, locale),
                zone.getId()
            ).asString(locale)
        }
    }
}
