package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.Quasicolon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShortParser extends NumberParser<Short> {
	public ShortParser(@NotNull Quasicolon bot) {
		this(bot, null);
	}

	public ShortParser(@NotNull Quasicolon bot, @Nullable ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Short decode(@NotNull String value) throws NumberFormatException {
		return Short.parseShort(value);
	}
}
