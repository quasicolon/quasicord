package dev.qixils.semicolon.variables.parsers.numbers;

import dev.qixils.semicolon.Semicolon;
import org.jetbrains.annotations.NotNull;

public final class DoubleParser extends NumberParser<Double> {
	public DoubleParser(@NotNull Semicolon bot) {
		super(bot, null);
	}

	public DoubleParser(@NotNull Semicolon bot, @NotNull ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Double fromDatabase(@NotNull String value) throws NumberFormatException {
		return Double.parseDouble(value);
	}
}
