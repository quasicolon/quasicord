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

public class UnknownTokenException extends InvalidSyntaxException {
	public UnknownTokenException(@NonNull CommandContext<?> context, @NonNull String token) {
		super(context, getErrorText(token));
	}

	public UnknownTokenException(@NonNull String namespace, @Nullable CommandArgument<?, ?> argument, @NonNull String token) {
		super(namespace, argument, getErrorText(token));
	}

	public UnknownTokenException(@NonNull Key argumentKey, @NonNull String token) {
		super(argumentKey, getErrorText(token));
	}

	private static @NonNull Text getErrorText(@NonNull String token) {
		return Text.single(Key.library("exception.unknown_token"), token);
	}
}
