package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;

public final class FloatParser extends NumberParser<Float> {
	public FloatParser(@NotNull QuasicolonBot bot) {
		super(bot, null);
	}

	public FloatParser(@NotNull QuasicolonBot bot, @NotNull ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Float fromDatabase(@NotNull String value) throws NumberFormatException {
		return Float.parseFloat(value);
	}
}
