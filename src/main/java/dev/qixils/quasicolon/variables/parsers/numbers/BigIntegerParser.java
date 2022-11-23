/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.Quasicord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

public class BigIntegerParser extends NumberParser<BigInteger> {
    public BigIntegerParser(@NotNull Quasicord bot) {
        super(bot, null);
    }

    public BigIntegerParser(@NotNull Quasicord bot, @Nullable ParserFilter filter) {
        super(bot, filter);
    }

    @Override
    public @NotNull BigInteger decode(@NotNull String value) throws NumberFormatException {
        return new BigInteger(value);
    }
}
