package dev.qixils.quasicolon.error.syntax;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class UnknownTokenException extends InvalidSyntaxException {
	public UnknownTokenException(@NonNull String namespace, @NonNull CommandContext<?> context, @NonNull String token) {
		super(namespace, context, getErrorText(token));
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
