/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.locale;

import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.checkerframework.checker.nullness.qual.Nullable;

record ContextImpl(
	long user,
	@Nullable DiscordLocale userLocale,
	long channel,
	long guild,
	@Nullable DiscordLocale guildLocale
) implements Context {
}

