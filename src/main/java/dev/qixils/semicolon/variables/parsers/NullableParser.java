package dev.qixils.semicolon.variables.parsers;

import dev.qixils.semicolon.Semicolon;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class NullableParser<R> extends VariableParser<R> {
    private final VariableParser<R> parser;
    private static final String NULL_VALUE = "null";

    public NullableParser(@NotNull Semicolon bot, @NotNull VariableParser<R> parser) {
        super(bot);
        this.parser = Objects.requireNonNull(parser, "parser cannot be null");
    }

    @Override
    public @Nullable R fromDatabase(@NotNull String value) {
        return value.equals(NULL_VALUE) ? null : parser.fromDatabase(value);
    }

    @Override
    public @NotNull String toDatabase(@Nullable R r) {
        if (r == null)
            return NULL_VALUE;

        String val = parser.toDatabase(r);
        if (val.equals(NULL_VALUE))
            throw new IllegalStateException("Supplied value resolved to the reserved keyword '" + NULL_VALUE + "'");

        return val;
    }

    @Override
    public @NotNull CompletableFuture<@Nullable R> parseText(@NotNull Message context, @NotNull String humanText) {
        if (humanText.equals(NULL_VALUE))
            return CompletableFuture.completedFuture(null);
        return parser.parseText(context, humanText);
    }
}
