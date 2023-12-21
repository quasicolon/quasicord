/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.autocomplete.impl;

import dev.qixils.quasicord.CommandManager;
import dev.qixils.quasicord.Key;
import dev.qixils.quasicord.autocomplete.AutoCompleter;
import dev.qixils.quasicord.locale.Context;
import dev.qixils.quasicord.text.Text;
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

	protected static String format(ZoneId zone, Locale locale) {
		return Text.single(Key.library("timezone_display"), zone.getDisplayName(TextStyle.FULL_STANDALONE, locale), zone.getId()).asString(locale);
	}

	@Override
	public @NonNull Flux<Command.@NotNull Choice> getSuggestions(@NonNull IAutoCompleteCallback event) {
		if (!(event instanceof CommandAutoCompleteInteraction interaction)) return Flux.empty();
		return commandManager.getLibrary()
			.getLocaleProvider()
			.forContext(Context.fromInteraction(event))
			.flatMapMany(locale -> {
				String input = interaction.getFocusedOption().getValue().toLowerCase(locale);
				return Flux.fromIterable(ZoneId.getAvailableZoneIds())
					.map(ZoneId::of)
					.filter(id -> {
						String compare = format(id, locale).toLowerCase(locale);
						return compare.contains(input) || compare.replaceAll("[/_]", " ").contains(input);
					})
					.sort(Comparator.comparing(id -> format(id, locale)))
					.take(OptionData.MAX_CHOICES)
					.map(id -> new Command.Choice(format(id, locale), id.getId()));
			});
	}
}
