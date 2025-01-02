/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.autocomplete.impl

import dev.qixils.quasicord.CommandManager
import dev.qixils.quasicord.autocomplete.AutoCompleter
import dev.qixils.quasicord.locale.Context
import net.dv8tion.jda.api.interactions.callbacks.IAutoCompleteCallback
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import reactor.core.publisher.Flux
import java.util.*
import java.util.function.Predicate

/**
 * Auto-completes a supported locale.
 */
class LocaleAutoCompleter(protected val commandManager: CommandManager) : AutoCompleter {
    override fun getSuggestions(event: IAutoCompleteCallback): Flux<Command.Choice> {
        if (event !is CommandAutoCompleteInteraction) return Flux.empty<Command.Choice>()
        return commandManager.library
            .localeProvider
            .forContext(Context.fromInteraction(event))
            .flatMapMany<Command.Choice> { displayLocale: Locale? ->
                val rawInput = event.focusedOption.value
                val enInput = rawInput.lowercase()
                val input = rawInput.lowercase(displayLocale!!)
                Flux.fromIterable(
                    commandManager.library
                        .translationProvider
                        .localesView
                )
                    .filter(Predicate { locale ->
                        locale.getDisplayName(displayLocale).lowercase(displayLocale)
                            .contains(input) || locale.getDisplayName(Locale.ENGLISH).lowercase()
                            .contains(enInput) || locale.toLanguageTag().lowercase().contains(enInput)
                    })
                    .sort(Comparator.comparing { locale -> locale!!.getDisplayName(displayLocale) })
                    .take(OptionData.MAX_CHOICES.toLong())
                    .map { locale ->
						Command.Choice(
							locale!!.getDisplayName(
								displayLocale
							), locale.toLanguageTag()
						)
					}
            }
    }
}
