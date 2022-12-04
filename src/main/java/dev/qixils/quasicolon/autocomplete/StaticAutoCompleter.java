/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.autocomplete;

import net.dv8tion.jda.api.interactions.callbacks.IAutoCompleteCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * An implementation for {@link AutoCompleter} which returns a static list of suggestions.
 */
public class StaticAutoCompleter implements AutoCompleter {
	private final @NotNull List<Command.Choice> suggestions;

	public StaticAutoCompleter() {
		this.suggestions = Collections.emptyList();
	}

	public StaticAutoCompleter(@NotNull List<Command.Choice> suggestions) {
		this.suggestions = suggestions;
	}

	@NotNull
	@Override
	public List<Command.Choice> getSuggestions(@NotNull IAutoCompleteCallback event) {
		return suggestions;
	}
}
