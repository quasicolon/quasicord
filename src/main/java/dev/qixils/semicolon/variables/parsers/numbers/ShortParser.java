package dev.qixils.semicolon.variables.parsers.numbers;

import dev.qixils.semicolon.Semicolon;
import org.jetbrains.annotations.NotNull;

public final class ShortParser extends NumberParser<Short> {
	public ShortParser(@NotNull Semicolon bot) {
		super(bot);
	}

	@Override
	public @NotNull Short fromDatabase(@NotNull String value) throws NumberFormatException {
		return Short.parseShort(value);
	}
}
