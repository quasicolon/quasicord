package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;

public final class ByteParser extends NumberParser<Byte> {
	public ByteParser(@NotNull QuasicolonBot bot) {
		super(bot, null);
	}

	public ByteParser(@NotNull QuasicolonBot bot, @NotNull ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Byte fromDatabase(@NotNull String value) throws NumberFormatException {
		return Byte.parseByte(value);
	}
}
