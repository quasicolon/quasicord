package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.Quasicolon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FloatParser extends NumberParser<Float> {
	public FloatParser(@NotNull Quasicolon bot) {
		this(bot, null);
	}

	public FloatParser(@NotNull Quasicolon bot, @Nullable ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Float decode(@NotNull String value) throws NumberFormatException {
		return Float.parseFloat(value);
	}
}
