package dev.qixils.semicolon.variables.parsers.numbers;

import dev.qixils.semicolon.Semicolon;
import org.jetbrains.annotations.NotNull;

public final class FloatParser extends NumberParser<Float> {
	public FloatParser(@NotNull Semicolon bot) {
		super(bot, null);
	}

	public FloatParser(@NotNull Semicolon bot, @NotNull ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Float fromDatabase(@NotNull String value) throws NumberFormatException {
		return Float.parseFloat(value);
	}
}
