package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;

public final class DoubleParser extends NumberParser<Double> {
	public DoubleParser(@NotNull QuasicolonBot bot) {
		super(bot, null);
	}

	public DoubleParser(@NotNull QuasicolonBot bot, @NotNull ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Double fromDatabase(@NotNull String value) throws NumberFormatException {
		return Double.parseDouble(value);
	}
}
