package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IntegerParser extends NumberParser<Integer> {
	public IntegerParser(@NotNull QuasicolonBot bot) {
		this(bot, null);
	}

	public IntegerParser(@NotNull QuasicolonBot bot, @Nullable ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Integer decode(@NotNull String value) throws NumberFormatException {
		return Integer.parseInt(value);
	}
}
