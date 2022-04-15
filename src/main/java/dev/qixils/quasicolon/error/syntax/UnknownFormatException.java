/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.error.syntax;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class UnknownFormatException extends InvalidSyntaxException {
	public UnknownFormatException(@NonNull CommandContext<?> context, @NonNull String input) {
		super(context, getErrorText(input));
	}

	public UnknownFormatException(@Nullable String namespace, @Nullable CommandArgument<?, ?> argument, @NonNull String input) {
		super(namespace, argument, getErrorText(input));
	}

	public UnknownFormatException(@NonNull Key argumentKey, @NonNull String input) {
		super(argumentKey, getErrorText(input));
	}

	private static @NonNull Text getErrorText(@NonNull String token) {
		return Text.single(Key.library("exception.unknown_format"), token);
	}
}
