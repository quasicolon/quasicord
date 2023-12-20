/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.autocomplete.impl;

import dev.qixils.quasicolon.CommandManager;
import dev.qixils.quasicolon.autocomplete.AutoCompleter;
import net.dv8tion.jda.api.interactions.callbacks.IAutoCompleteCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;

import java.time.ZoneId;
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
		String input = interaction.getFocusedOption().getValue().toLowerCase(Locale.ENGLISH);
		// TODO: display name auirhaujfdnmjs
		return Flux.fromStream(ZoneId.getAvailableZoneIds()
				.stream()
				.filter(id -> id.toLowerCase(Locale.ENGLISH).contains(input))
				.sorted()
				.map(id -> new Command.Choice(id, id)))
			.take(OptionData.MAX_CHOICES);
	}
}
