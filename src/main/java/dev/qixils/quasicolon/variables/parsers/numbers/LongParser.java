package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;

public final class LongParser extends NumberParser<Long> {
	public LongParser(@NotNull QuasicolonBot bot) {
		super(bot, null);
	}

	public LongParser(@NotNull QuasicolonBot bot, @NotNull ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Long fromDatabase(@NotNull String value) throws NumberFormatException {
		return Long.parseLong(value);
	}
}
