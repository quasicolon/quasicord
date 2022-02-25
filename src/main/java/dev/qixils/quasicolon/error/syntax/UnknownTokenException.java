package dev.qixils.quasicolon.error.syntax;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import dev.qixils.quasicolon.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class UnknownTokenException extends InvalidSyntaxException {
	public UnknownTokenException(@NonNull CommandContext<?> context, @NonNull String token) {
		super(context, getErrorText(token));
	}

	public UnknownTokenException(@Nullable CommandArgument<?, ?> argument, @NonNull String token) {
		super(argument, getErrorText(token));
	}

	public UnknownTokenException(@NonNull String argumentKey, @NonNull String token) {
		super(argumentKey, getErrorText(token));
	}

	private static @NonNull Text getErrorText(@NonNull String token) {
		// TODO: create localizable text
	}
}
