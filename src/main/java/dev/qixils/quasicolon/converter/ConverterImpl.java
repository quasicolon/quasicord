/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.converter;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.internal.utils.ChannelUtil;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.BiFunction;

public class ConverterImpl<I, O> extends AbstractConverter<I, O> {

	private final @NonNull BiFunction<Interaction, I, O> converter;

	public ConverterImpl(
			@NonNull Class<I> inputClass,
			@NonNull Class<O> outputClass,
			@NonNull BiFunction<Interaction, I, O> converter
	) {
		super(inputClass, outputClass);
		this.converter = converter;
	}

	@Override
	public @NonNull O convert(@NonNull Interaction interaction, @NonNull I input) {
		return converter.apply(interaction, input);
	}

	@NonNull
	public static <O extends Channel> Converter<Channel, O> channel(@NonNull Class<O> outputClass) {
		return new ConverterImpl<>(Channel.class, outputClass, (it, channel) -> ChannelUtil.safeChannelCast(channel, outputClass));
	}

	@NonNull
	public static <T> Converter<T, T> identity(@NonNull Class<T> clazz) {
		return new ConverterImpl<>(clazz, clazz, (it, input) -> input);
	}
}
