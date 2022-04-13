package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.Quasicolon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DoubleParser extends NumberParser<Double> {
	public DoubleParser(@NotNull Quasicolon bot) {
		this(bot, null);
	}

	public DoubleParser(@NotNull Quasicolon bot, @Nullable ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Double decode(@NotNull String value) throws NumberFormatException {
		return Double.parseDouble(value);
	}
}
