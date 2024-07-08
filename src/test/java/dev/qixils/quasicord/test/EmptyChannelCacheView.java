/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.test;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.utils.cache.ChannelCacheView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmptyChannelCacheView<T extends Channel> extends EmptySnowflakeCacheView<T> implements ChannelCacheView<T> {
	@NotNull
	@Override
	public <C extends T> ChannelCacheView<C> ofType(@NotNull Class<C> type) {
		return (ChannelCacheView<C>) this;
	}

	@Nullable
	@Override
	public T getElementById(@NotNull ChannelType type, long id) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	private static final EmptyChannelCacheView EMPTY_CHANNEL_CACHE_VIEW = new EmptyChannelCacheView();

	public static <T extends Channel> ChannelCacheView<T> emptyChannelCacheView() {
		//noinspection unchecked
		return (ChannelCacheView<T>) EMPTY_CHANNEL_CACHE_VIEW;
	}
}
