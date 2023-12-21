/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.locale.impl;

import dev.qixils.quasicord.locale.Context;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public record ImmutableContextImpl(
		long user,
		@Nullable DiscordLocale userLocale,
		long channel,
		long guild,
		@Nullable DiscordLocale guildLocale
) implements Context {

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public @NotNull Context user(long user) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NonNull Context userLocale(@Nullable DiscordLocale locale) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull Context channel(long channel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull Context guild(long guild) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NonNull Context guildLocale(@Nullable DiscordLocale locale) {
		throw new UnsupportedOperationException();
	}
}

