/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.converter;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.internal.utils.Helpers;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class ConverterImpl<I, O> extends AbstractConverter<I, O> {

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
	public static <O> ConverterImpl<Void, O> contextual(@NonNull Class<O> outputClass, @NonNull Function<Interaction, O> converter) {
		return new ConverterImpl<>(Void.class, outputClass, (interaction, input) -> converter.apply(interaction));
	}

	@NonNull
	public static <O extends Channel> ConverterImpl<Channel, O> channel(@NonNull Class<O> outputClass) {
		return new ConverterImpl<>(Channel.class, outputClass, (it, channel) -> Helpers.safeChannelCast(channel, outputClass));
	}

	@NonNull
	public static <T> ConverterImpl<T, T> identity(@NonNull Class<T> clazz) {
		return new ConverterImpl<>(clazz, clazz, (it, input) -> input);
	}
}
