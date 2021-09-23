package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ByteParser extends NumberParser<Byte> {
	public ByteParser(@NotNull QuasicolonBot bot) {
		this(bot, null);
	}

	public ByteParser(@NotNull QuasicolonBot bot, @Nullable ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Byte decode(@NotNull String value) throws NumberFormatException {
		return Byte.parseByte(value);
	}
}
