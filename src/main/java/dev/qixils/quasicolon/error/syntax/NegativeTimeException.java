package dev.qixils.quasicolon.error.syntax;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import dev.qixils.quasicolon.locale.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class NegativeTimeException extends InvalidSyntaxException {
    public NegativeTimeException(@NonNull CommandContext<?> context) {
        super(context, getErrorText());
    }

    public NegativeTimeException(@Nullable CommandArgument<?, ?> argument) {
        super(argument, getErrorText());
    }

    public NegativeTimeException(@NonNull String argumentKey) {
        super(argumentKey, getErrorText());
    }

    private static @NonNull Text getErrorText() {
        // TODO: create localizable text with key "exception.negative_time"
    }
}
