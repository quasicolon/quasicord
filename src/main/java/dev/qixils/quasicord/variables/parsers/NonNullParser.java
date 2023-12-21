/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.variables.parsers;

import dev.qixils.quasicord.Quasicord;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * A variable parser whose parsed values should never be null, i.e. the values should always be available.
 * This is used for primitive objects such as integers and strings.
 * @param <R>
 */
public abstract class NonNullParser<R> extends VariableParser<R> {
	public NonNullParser(@NotNull Quasicord bot) {
		super(bot);
	}

	@Override
	public final @NotNull Mono<@NotNull R> fromDatabase(long guild, @NotNull String variable) {
		return super.fromDatabase(guild, variable);
	}

	@Override
	public abstract @NotNull R decode(@NotNull String value);
}
