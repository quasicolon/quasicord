package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.Quasicolon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

public class BigIntegerParser extends NumberParser<BigInteger> {
    public BigIntegerParser(@NotNull Quasicolon bot) {
        super(bot, null);
    }

    public BigIntegerParser(@NotNull Quasicolon bot, @Nullable ParserFilter filter) {
        super(bot, filter);
    }

    @Override
    public @NotNull BigInteger decode(@NotNull String value) throws NumberFormatException {
        return new BigInteger(value);
    }
}
