package dev.qixils.semicolon.variables.parsers.numbers;

import dev.qixils.semicolon.Semicolon;
import org.jetbrains.annotations.NotNull;

public final class IntegerParser extends NumberParser<Integer> {
	public IntegerParser(@NotNull Semicolon bot) {
		super(bot);
	}

	@Override
	public @NotNull Integer fromDatabase(@NotNull String value) throws NumberFormatException {
		return Integer.parseInt(value);
	}
}
