package dev.qixils.quasicolon.error.syntax;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import dev.qixils.quasicolon.error.LocalizedRuntimeException;
import dev.qixils.quasicolon.locale.TranslationProvider.Type;
import dev.qixils.quasicolon.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

public class InvalidSyntaxException extends LocalizedRuntimeException {
    private final @NonNull Text subError;
    private final @NonNull String argumentNameKey;

    // todo: allow configuring of the translation provider used for the argumentNameKey
    //   or create some sort of static utility method to create a Text given a Command-whatever

    public InvalidSyntaxException(@NonNull CommandContext<?> context, @NonNull Text subError) {
        this(context.getCurrentArgument(), subError);
    }

    public InvalidSyntaxException(@Nullable CommandArgument<?, ?> argument, @NonNull Text subError) {
        this(argument == null ? "arg._unknown_" : argument.getName(), subError);
    }

    public InvalidSyntaxException(@NonNull String argumentKey, @NonNull Text subError) {
        super(Type.LIBRARY, "exception.invalid_syntax");
        this.subError = subError;
        this.argumentNameKey = argumentKey;
    }

    @Override
    public @NonNull String asString(@NonNull Locale locale) {
        return new MessageFormat(translationProvider.getSingle(getKey(), locale).get(), locale)
                .format(new Object[]{
                        translationProvider.getSingle(argumentNameKey, locale).get(),
                        subError.asString(locale)
                });
    }
}
