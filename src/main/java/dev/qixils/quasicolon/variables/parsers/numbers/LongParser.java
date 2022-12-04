/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.Quasicord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LongParser extends NumberParser<Long> {
	public LongParser(@NotNull Quasicord bot) {
		this(bot, null);
	}

	public LongParser(@NotNull Quasicord bot, @Nullable ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Long decode(@NotNull String value) throws NumberFormatException {
		return Long.parseLong(value);
	}
}
