/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.autocomplete.impl;

import dev.qixils.quasicolon.CommandManager;
import dev.qixils.quasicolon.autocomplete.AutoCompleter;
import dev.qixils.quasicolon.locale.Context;
import net.dv8tion.jda.api.interactions.callbacks.IAutoCompleteCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.Locale;

/**
 * Auto-completes a supported locale.
 */
public class LocaleAutoCompleter implements AutoCompleter {

	protected final CommandManager commandManager;

	public LocaleAutoCompleter(CommandManager commandManager) {
		this.commandManager = commandManager;
	}

	@Override
	public @NonNull Flux<Command.@NotNull Choice> getSuggestions(@NonNull IAutoCompleteCallback event) {
		if (!(event instanceof CommandAutoCompleteInteraction interaction)) return Flux.empty();
		return commandManager.getLibrary()
			.getLocaleProvider()
			.forContext(Context.fromInteraction(event))
			.flatMapMany(displayLocale -> {
				String rawInput = interaction.getFocusedOption().getValue();
				String enInput = rawInput.toLowerCase(Locale.ENGLISH);
				String input = rawInput.toLowerCase(displayLocale);
				return Flux.fromIterable(commandManager.getLibrary()
						.getTranslationProvider()
						.getLocales())
					.filter(locale -> locale.getDisplayName(displayLocale).toLowerCase(displayLocale).contains(input) || locale.getDisplayName(Locale.ENGLISH).toLowerCase(Locale.ENGLISH).contains(enInput) || locale.toLanguageTag().toLowerCase(Locale.ENGLISH).contains(enInput))
					.sort(Comparator.comparing(locale -> locale.getDisplayName(displayLocale)))
					.take(OptionData.MAX_CHOICES)
					.map(locale -> new Command.Choice(locale.getDisplayName(displayLocale), locale.toLanguageTag()));
			});
	}
}
