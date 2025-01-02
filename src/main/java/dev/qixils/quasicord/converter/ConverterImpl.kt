/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.converter;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.internal.utils.ChannelUtil;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.BiFunction;

public class ConverterImpl<I, O> extends AbstractConverter<I, O> {

	@FunctionalInterface
	public interface ConverterImplStep<I, O> {
		/**
		 * Converts an input to the output type.
		 *
		 * @param interaction the interaction being invoked
		 * @param input       the user input
		 * @param targetClass the class to convert to
		 * @return converted value
		 */
		@NonNull
		O convert(@NonNull Interaction interaction, @NonNull I input, @NonNull Class<? extends O> targetClass);
	}

	private final @NonNull ConverterImplStep<I, O> converter;

	public ConverterImpl(
		@NonNull Class<I> inputClass,
		@NonNull Class<O> outputClass,
		@NonNull ConverterImplStep<I, O> converter
	) {
		super(inputClass, outputClass);
		this.converter = converter;
	}

	public ConverterImpl(
			@NonNull Class<I> inputClass,
			@NonNull Class<O> outputClass,
			@NonNull BiFunction<Interaction, I, O> converter
	) {
		this(inputClass, outputClass, (ctx, i, t) -> converter.apply(ctx, i));
	}

	@Override
	public @NonNull O convert(@NonNull Interaction interaction, @NonNull I input, @NonNull Class<? extends O> targetClass) {
		return converter.convert(interaction, input, targetClass);
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
