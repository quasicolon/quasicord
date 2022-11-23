/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.error.syntax;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.Quasicord;
import dev.qixils.quasicolon.error.LocalizedRuntimeException;
import dev.qixils.quasicolon.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class InvalidSyntaxException extends LocalizedRuntimeException {
	private static final @NonNull Key UNKNOWN_ARGUMENT = Key.library("arg._unknown_");

	public InvalidSyntaxException(@NonNull CommandContext<?> context, @NonNull Text subError) {
		this(context.getOrDefault(Quasicord.NAMESPACE_KEY, null), context.getCurrentArgument(), subError);
	}

	public InvalidSyntaxException(@Nullable String namespace, @Nullable CommandArgument<?, ?> argument, @NonNull Text subError) {
		this(argument == null || namespace == null
				? UNKNOWN_ARGUMENT
				: new Key(namespace, argument.getName()),
				subError
		);
	}

	public InvalidSyntaxException(@NonNull Key argumentKey, @NonNull Text subError) {
		super(Text.single(
				Key.library("exception.invalid_syntax"),
				Text.single(argumentKey),
				subError
		));
	}
}
