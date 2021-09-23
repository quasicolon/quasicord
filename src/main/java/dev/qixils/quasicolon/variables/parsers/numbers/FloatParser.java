package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FloatParser extends NumberParser<Float> {
	public FloatParser(@NotNull QuasicolonBot bot) {
		this(bot, null);
	}

	public FloatParser(@NotNull QuasicolonBot bot, @Nullable ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Float decode(@NotNull String value) throws NumberFormatException {
		return Float.parseFloat(value);
	}
}
