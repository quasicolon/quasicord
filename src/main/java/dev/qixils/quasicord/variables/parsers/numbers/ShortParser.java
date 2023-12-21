/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.variables.parsers.numbers;

import dev.qixils.quasicord.Quasicord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShortParser extends NumberParser<Short> {
	public ShortParser(@NotNull Quasicord bot) {
		this(bot, null);
	}

	public ShortParser(@NotNull Quasicord bot, @Nullable ParserFilter filter) {
		super(bot, filter);
	}

	@Override
	public @NotNull Short decode(@NotNull String value) throws NumberFormatException {
		return Short.parseShort(value);
	}
}
