/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.variables.parsers;

import dev.qixils.quasicord.Quasicord;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class NullableParser<R> extends VariableParser<R> {
    protected final VariableParser<R> parser;
    protected static final String NULL_VALUE = "$\u2603"; // snowman! :)

    public NullableParser(@NotNull Quasicord bot, @NotNull VariableParser<R> parser) {
        super(bot);
        this.parser = Objects.requireNonNull(parser, "parser cannot be null");
    }

    @Override
    public @Nullable R decode(@NotNull String value) {
        return value.equals(NULL_VALUE) ? null : parser.decode(value);
    }

    @Override
    public @NotNull String encode(@Nullable R r) {
        if (r == null)
            return NULL_VALUE;

        String val = parser.encode(r);
        if (val.equals(NULL_VALUE))
            throw new IllegalStateException("Supplied value resolved to the reserved keyword '" + NULL_VALUE + "'");

        return val;
    }

    @Override
    public @NotNull CompletableFuture<@Nullable R> parseText(@Nullable Message context, @NotNull String humanText) {
        if (humanText.equals(NULL_VALUE))
            return CompletableFuture.completedFuture(null);
        return parser.parseText(context, humanText);
    }
}
