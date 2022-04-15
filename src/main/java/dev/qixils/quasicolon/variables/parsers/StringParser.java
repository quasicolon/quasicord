/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.variables.parsers;

import dev.qixils.quasicolon.Quasicolon;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class StringParser extends VariableParser<String> {
	public StringParser(@NotNull Quasicolon bot) {
		super(bot);
	}

	@Override
	public @NotNull String decode(@NotNull String value) {
		return value;
	}

	@Override
	public @NotNull String encode(@NotNull String string) {
		return string;
	}

	@Override
	public @NotNull CompletableFuture<@Nullable String> parseText(@Nullable Message context, @NotNull String humanText) {
		return CompletableFuture.completedFuture(humanText);
	}
}
