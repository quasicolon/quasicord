package dev.qixils.quasicolon.error.syntax;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import dev.qixils.quasicolon.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class IllegalQuotationsException extends InvalidSyntaxException {
    public IllegalQuotationsException(@NonNull CommandContext<?> context) {
        super(context, getErrorText());
    }

    public IllegalQuotationsException(@Nullable CommandArgument<?, ?> argument) {
        super(argument, getErrorText());
    }

    public IllegalQuotationsException(@NonNull String argumentKey) {
        super(argumentKey, getErrorText());
    }

    private static @NonNull Text getErrorText() {
        // TODO: create localizable text with key "exception.invalid_syntax.quotations"
    }
}
