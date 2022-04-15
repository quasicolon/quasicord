/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.arguments;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.context.CommandContext;
import dev.qixils.quasicolon.error.syntax.IllegalQuotationsException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

abstract class AutomaticTypedParser<C, T> extends AbstractTypedParser<C, T> {
	AutomaticTypedParser(@NotNull ParserMode mode) {
		super(mode);
	}

	@Override
	protected @NonNull ArgumentParseResult<@NonNull T> parseQuoted(@NonNull CommandContext<@NonNull C> ctx, @NonNull Queue<@NonNull String> inputQueue) {
		assert !inputQueue.isEmpty();

		String first = inputQueue.peek();

		// handle quoted input
		// first ensures the initial string is at least 2 characters long,
		//     starts with a quotation mark, and does not have another quotation mark inside it
		if (!first.isEmpty() && first.charAt(0) == '"' && first.indexOf("\"", 1) == -1) {
			if (first.length() > 2 && first.indexOf("\"", 1) != -1)
				return ArgumentParseResult.failure(new IllegalQuotationsException(ctx));

			int idx = -1; // will get incremented to 0 at start of loop

			List<String> inputSequence = new ArrayList<>();
			inputSequence.add(first.substring(1));

			for (String item : inputQueue) {
				idx += 1;
				if (idx == 0)
					continue;

				int quoteIndex = item.indexOf('"');
				// if no quotation in token, continue
				if (quoteIndex == -1)
					inputSequence.add(item);
					// if quotation is in the right place, end iteration
				else if (quoteIndex == item.length()-1) {
					// add string without the quotation mark
					inputSequence.add(item.substring(0, item.length()-1));
					// remove all the items that we're using from queue
					while (idx >= 0) {
						idx -= 1;
						inputQueue.remove();
					}
					// parse
					return parse(ctx, inputSequence);
				}

				// token has an invalid quotation mark in it (mid-token)
				return ArgumentParseResult.failure(new IllegalQuotationsException(ctx));
			}
			// a closing quotation mark was not found
			return ArgumentParseResult.failure(new IllegalQuotationsException(ctx));
		}
		// parse just the first token
		return parse(ctx, Collections.singletonList(inputQueue.remove()));
	}

	@Override
	protected @NonNull ArgumentParseResult<@NonNull T> parseAll(@NonNull CommandContext<@NonNull C> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
		assert !inputQueue.isEmpty();

		List<String> inputSequence = new ArrayList<>(inputQueue.size());
		while (!inputQueue.isEmpty())
			inputSequence.add(inputQueue.remove());
		return parse(commandContext, inputSequence);
	}

	@Override
	protected @NonNull ArgumentParseResult<@NonNull T> parseGreedy(@NonNull CommandContext<@NonNull C> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
		assert !inputQueue.isEmpty();

		// tries to find the largest number of input tokens that can be parsed
		List<String> inputSequence = new ArrayList<>();
		ArgumentParseResult<T> previousParseResult = null;
		while (!inputQueue.isEmpty() && (previousParseResult == null || previousParseResult.getFailure().isEmpty())) {
			inputSequence.add(inputQueue.element());
			previousParseResult = parse(commandContext, new ArrayList<>(inputSequence));
			if (previousParseResult.getFailure().isEmpty())
				inputQueue.remove();
		}
		return previousParseResult;
	}

	// TODO javadoc
	protected abstract @NonNull ArgumentParseResult<@NonNull T> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull List<@NonNull String> arguments);
}
