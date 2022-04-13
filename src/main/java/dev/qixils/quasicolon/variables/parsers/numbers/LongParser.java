package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.Quasicolon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LongParser extends NumberParser<Long> {
	public LongParser(@NotNull Quasicolon bot) {
		this(bot, null);
	}

	public LongParser(@NotNull Quasicolon bot, @Nullable ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Long decode(@NotNull String value) throws NumberFormatException {
		return Long.parseLong(value);
	}
}
