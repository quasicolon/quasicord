package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LongParser extends NumberParser<Long> {
	public LongParser(@NotNull QuasicolonBot bot) {
		this(bot, null);
	}

	public LongParser(@NotNull QuasicolonBot bot, @Nullable ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Long decode(@NotNull String value) throws NumberFormatException {
		return Long.parseLong(value);
	}
}
