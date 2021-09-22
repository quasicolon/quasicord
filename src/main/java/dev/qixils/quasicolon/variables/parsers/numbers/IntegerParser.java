package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;

public final class IntegerParser extends NumberParser<Integer> {
	public IntegerParser(@NotNull QuasicolonBot bot) {
		super(bot, null);
	}

	public IntegerParser(@NotNull QuasicolonBot bot, @NotNull ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Integer fromDatabase(@NotNull String value) throws NumberFormatException {
		return Integer.parseInt(value);
	}
}
