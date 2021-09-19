package dev.qixils.semicolon.variables.parsers.numbers;

import dev.qixils.semicolon.Semicolon;
import org.jetbrains.annotations.NotNull;

public final class LongParser extends NumberParser<Long> {
	public LongParser(@NotNull Semicolon bot) {
		super(bot);
	}

	@Override
	public @NotNull Long fromDatabase(@NotNull String value) throws NumberFormatException {
		return Long.parseLong(value);
	}
}
