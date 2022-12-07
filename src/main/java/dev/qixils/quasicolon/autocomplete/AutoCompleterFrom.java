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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An object which supplies pre-defined auto-complete suggestions for {@link IAutoCompleteCallback}s.
 */
public class AutoCompleterFrom implements AutoCompleter {
	private final Collection<Command.Choice> choices;

	public AutoCompleterFrom(List<Command.Choice> choices) {
		this.choices = new ArrayList<>(choices);
	}

	public AutoCompleterFrom(Command.Choice... choices) {
		this.choices = List.of(choices);
	}

	@Override
	public @NonNull Collection<Command.@NotNull Choice> getSuggestions(@NonNull IAutoCompleteCallback event) {
		return choices;
	}
}
