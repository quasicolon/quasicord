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

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.Locale;

/**
 * Auto-completes a regional timezone.
 */
public class TimeZoneAutoCompleter implements AutoCompleter {

	protected final CommandManager commandManager;

	public TimeZoneAutoCompleter(CommandManager commandManager) {
		this.commandManager = commandManager;
	}

	@Override
	public @NonNull Flux<Command.@NotNull Choice> getSuggestions(@NonNull IAutoCompleteCallback event) {
		if (!(event instanceof CommandAutoCompleteInteraction interaction)) return Flux.empty();
		return commandManager.getLibrary()
			.getLocaleProvider()
			.forContext(Context.fromInteraction(event))
			.flatMapMany(locale -> {
				String rawInput = interaction.getFocusedOption().getValue();
				String enInput = rawInput.toLowerCase(Locale.ENGLISH);
				String input = rawInput.toLowerCase(locale);
				return Flux.fromIterable(ZoneId.getAvailableZoneIds())
					.map(ZoneId::of)
					.filter(id -> id.getId().toLowerCase(Locale.ENGLISH).contains(enInput) || id.getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH).toLowerCase(Locale.ENGLISH).contains(enInput) || id.getDisplayName(TextStyle.FULL_STANDALONE, locale).toLowerCase(locale).contains(input))
					.sort(Comparator.comparing(id -> id.getDisplayName(TextStyle.FULL_STANDALONE, locale).toLowerCase(locale)))
					.take(OptionData.MAX_CHOICES)
					.map(id -> new Command.Choice(id.getDisplayName(TextStyle.FULL_STANDALONE, locale), id.getId()));
			});
	}
}
