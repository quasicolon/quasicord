package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DoubleParser extends NumberParser<Double> {
	public DoubleParser(@NotNull QuasicolonBot bot) {
		this(bot, null);
	}

	public DoubleParser(@NotNull QuasicolonBot bot, @Nullable ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Double decode(@NotNull String value) throws NumberFormatException {
		return Double.parseDouble(value);
	}
}
