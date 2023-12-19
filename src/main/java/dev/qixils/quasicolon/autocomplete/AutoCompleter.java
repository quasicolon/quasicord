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

/**
 * An object which supplies auto-complete suggestions for {@link IAutoCompleteCallback}s.
 * Implementations of this interface are expected to have a public constructor with no parameters.
 */
public interface AutoCompleter {

	/**
	 * Returns a list of suggestions for the given event.
	 *
	 * @param event event to get suggestions for
	 * @return list of suggestions
	 */
	@NonNull Flux<Command.@NotNull Choice> getSuggestions(@NonNull IAutoCompleteCallback event);
}
