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
import java.util.*

/**
 * Auto-completes a supported locale.
 */
class LocaleAutoCompleter(protected val commandManager: CommandManager) : AutoCompleter {
    override suspend fun getSuggestions(event: IAutoCompleteCallback): List<Command.Choice> {
        if (event !is CommandAutoCompleteInteraction) return emptyList()
		val displayLocale = commandManager.library
			.localeProvider
			.forContext(Context.fromInteraction(event))
		val rawInput = event.focusedOption.value
		val enInput = rawInput.lowercase()
		val input = rawInput.lowercase(displayLocale)
		return commandManager.library
			.translationProvider
			.localesView
			.filter { locale ->
				locale.getDisplayName(displayLocale).lowercase(displayLocale)
					.contains(input) || locale.getDisplayName(Locale.ENGLISH).lowercase()
					.contains(enInput) || locale.toLanguageTag().lowercase().contains(enInput)
			}
			.sortedBy { locale -> locale.getDisplayName(displayLocale) }
			.take(OptionData.MAX_CHOICES)
			.map { locale -> Command.Choice(locale.getDisplayName(displayLocale), locale.toLanguageTag()) }
    }
}
