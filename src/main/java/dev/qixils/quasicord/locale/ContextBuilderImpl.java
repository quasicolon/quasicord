/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.locale;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Accessors(fluent = true, chain = true)
@NoArgsConstructor
@Data
final class ContextBuilderImpl implements Context.Builder {
	private long user;
	private @Nullable DiscordLocale userLocale;
	private long channel;
	private long guild;
	private @Nullable DiscordLocale guildLocale;

	@Override
	public @NonNull Context build() {
		return new ContextImpl(user, userLocale, channel, guild, guildLocale);
	}
}
