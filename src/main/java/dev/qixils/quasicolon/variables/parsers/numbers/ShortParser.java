package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;

public final class ShortParser extends NumberParser<Short> {
	public ShortParser(@NotNull QuasicolonBot bot) {
		super(bot, null);
	}

	public ShortParser(@NotNull QuasicolonBot bot, @NotNull ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Short fromDatabase(@NotNull String value) throws NumberFormatException {
		return Short.parseShort(value);
	}
}
