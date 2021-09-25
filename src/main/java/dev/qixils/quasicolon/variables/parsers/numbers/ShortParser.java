package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShortParser extends NumberParser<Short> {
	public ShortParser(@NotNull QuasicolonBot bot) {
		this(bot, null);
	}

	public ShortParser(@NotNull QuasicolonBot bot, @Nullable ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Short decode(@NotNull String value) throws NumberFormatException {
		return Short.parseShort(value);
	}
}
