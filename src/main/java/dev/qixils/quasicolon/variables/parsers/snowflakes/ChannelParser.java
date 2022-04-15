/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.variables.parsers.snowflakes;

import dev.qixils.quasicolon.Quasicolon;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChannelParser<R extends GuildChannel> extends SnowflakeParser<R> {
	private final @NonNull Class<R> channelClass;

	public ChannelParser(@NonNull Quasicolon bot, @NonNull Class<R> channelClass) {
		super(bot);
		this.channelClass = channelClass;
	}

	@Override
	public @Nullable R decode(@NotNull String value) {
		GuildChannel gc = bot.getJDA().getGuildChannelById(value);
		if (channelClass.isInstance(gc))
			//noinspection unchecked
			return (R) gc;
		return null;
	}

	@Override
	public @NotNull CompletableFuture<@Nullable R> parseText(@Nullable Message context, @NotNull String humanText) {
		return super.parseText(context, humanText).thenApplyAsync(superChannel -> {
			if (superChannel != null || context == null)
				return superChannel;

			List<GuildChannel> allChannels = context.getGuild().getChannels();
			List<R> filteredChannels = new ArrayList<>();
			for (GuildChannel chan : allChannels) {
				if (channelClass.isInstance(chan))
					//noinspection unchecked
					filteredChannels.add((R) chan);
			}
			List<Long> attempted = new ArrayList<>();

			for (R channel : filteredChannels) {
				if (channel.getName().equalsIgnoreCase(humanText) && ask(context, channel, attempted))
					return channel;
			}

			final String lowerText = humanText.toLowerCase();
			for (R channel : filteredChannels) {
				if (channel.getName().toLowerCase().startsWith(lowerText) && ask(context, channel, attempted))
					return channel;
			}

			for (R channel : filteredChannels) {
				if (channel.getName().toLowerCase().contains(lowerText) && ask(context, channel, attempted))
					return channel;
			}

			return null;
		});
	}
}
