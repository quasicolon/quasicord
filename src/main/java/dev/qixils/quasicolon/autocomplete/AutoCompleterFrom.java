/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.autocomplete;

import net.dv8tion.jda.api.interactions.callbacks.IAutoCompleteCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * An object which supplies pre-defined auto-complete suggestions for {@link IAutoCompleteCallback}s.
 */
public class AutoCompleterFrom implements AutoCompleter {
	private final Flux<Command.Choice> choices;

	public AutoCompleterFrom(List<Command.Choice> choices) {
		this.choices = Flux.fromIterable(choices);
	}

	public AutoCompleterFrom(Command.Choice... choices) {
		this.choices = Flux.fromArray(choices);
	}

	@Override
	public @NonNull Flux<Command.@NotNull Choice> getSuggestions(@NonNull IAutoCompleteCallback event) {
		return choices;
	}
}
