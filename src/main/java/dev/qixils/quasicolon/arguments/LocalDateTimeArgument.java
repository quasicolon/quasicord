/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.arguments;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class LocalDateTimeArgument<C> extends CommandArgument<C, LocalDateTime> {

    private static <C> ArgumentParser<C, LocalDateTime> parser(@NonNull Function<C, Optional<ZoneId>> senderTimezoneMapper,
                                                               @NonNull ParserMode mode,
                                                               boolean futureOnly) {
        return new ZonedDateTimeArgument.ZonedDateTimeParser<>(senderTimezoneMapper, mode, futureOnly)
                .map(($, zonedDateTime) -> ArgumentParseResult.success(zonedDateTime.toLocalDateTime()));
    }

    LocalDateTimeArgument(
            final boolean required,
            final @NonNull String name,
            final @NonNull Function<@NonNull C, @NonNull Optional<ZoneId>> senderTimezoneMapper,
            final @NonNull ParserMode mode,
            final boolean futureOnly,
            final @NonNull String defaultValue,
            final @NonNull TypeToken<LocalDateTime> valueType,
            final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
            final @NonNull ArgumentDescription defaultDescription
    ) {
        super(required, name, parser(senderTimezoneMapper, mode, futureOnly), defaultValue, valueType, suggestionsProvider, defaultDescription);
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, LocalDateTime> {

        private Builder(final @NonNull String name) {
            super(LocalDateTime.class, name);
        }

        private @NonNull ParserMode parserMode = ParserMode.QUOTED;
        private @Nullable Function<C, Optional<ZoneId>> senderTimezoneMapper;
        private boolean futureOnly = false;

        public @NonNull Builder<C> mode(@NonNull ParserMode mode) {
            this.parserMode = mode;
            return this;
        }

        public @NonNull Builder<C> senderTimezoneMapper(@NonNull Function<@NonNull C, @NonNull Optional<ZoneId>> senderTimezoneMapper) {
            this.senderTimezoneMapper = senderTimezoneMapper;
            return this;
        }

        public @NonNull Builder<C> futureOnly(boolean futureOnly) {
            this.futureOnly = futureOnly;
            return this;
        }

        @Override
        public @NonNull CommandArgument<@NonNull C, @NonNull LocalDateTime> build() {
            if (senderTimezoneMapper == null)
                throw new IllegalStateException("Sender timezone mapper is not set");
            return new LocalDateTimeArgument<>(isRequired(), getName(), senderTimezoneMapper, parserMode, futureOnly, getDefaultValue(), getValueType(), getSuggestionsProvider(), getDefaultDescription());
        }

    }
}
