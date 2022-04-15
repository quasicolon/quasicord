/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.arguments;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Allows easy parsing of arguments with a {@link ParserMode}.
 * @param <C> Command sender type
 * @param <T> Value type
 */
abstract class AbstractTypedParser<C, T> implements ArgumentParser<C, T> {
	protected final @NotNull ParserMode mode;
	AbstractTypedParser(final @NotNull ParserMode mode) {
		this.mode = mode;
	}

	@Override
	public @NonNull ArgumentParseResult<@NonNull T> parse(final @NonNull CommandContext<@NonNull C> commandContext, final @NonNull Queue<@NonNull String> inputQueue) {
		if (inputQueue.isEmpty())
			return ArgumentParseResult.failure(new NoSuchElementException("inputQueue cannot be empty"));

		if (mode == ParserMode.QUOTED) {
			return parseQuoted(commandContext, inputQueue);
		} else if (mode == ParserMode.ALL) {
			return parseAll(commandContext, inputQueue);
		} else if (mode == ParserMode.GREEDY) {
			return parseQuoted(commandContext, inputQueue);
		}

		return ArgumentParseResult.failure(new IllegalStateException("Unknown ParserMode " + mode.name()));
	}

	protected abstract @NonNull ArgumentParseResult<@NonNull T> parseQuoted(final @NonNull CommandContext<@NonNull C> commandContext, final @NonNull Queue<@NonNull String> inputQueue);

	protected abstract @NonNull ArgumentParseResult<@NonNull T> parseAll(final @NonNull CommandContext<@NonNull C> commandContext, final @NonNull Queue<@NonNull String> inputQueue);

	protected abstract @NonNull ArgumentParseResult<@NonNull T> parseGreedy(final @NonNull CommandContext<@NonNull C> commandContext, final @NonNull Queue<@NonNull String> inputQueue);

}
