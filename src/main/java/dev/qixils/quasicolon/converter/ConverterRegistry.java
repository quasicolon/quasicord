/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.converter;

import dev.qixils.quasicolon.Quasicord;
import dev.qixils.quasicolon.converter.impl.ZonedDateTimeConverter;
import dev.qixils.quasicolon.registry.impl.RegistryImpl;
import net.dv8tion.jda.api.interactions.Interaction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public final class ConverterRegistry extends RegistryImpl<Converter<?, ?>> {

	public final Converter<String, ZonedDateTime> ZONED_DATE_TIME;

	public ConverterRegistry(@NonNull Quasicord library) {
		super("converters");
		// zoned date time and misc conversions to other java time types
		ZONED_DATE_TIME = typedRegister(new ZonedDateTimeConverter(library));
		register(new ConverterImpl<>(ZonedDateTime.class, Instant.class, (it, zdt) -> zdt.toInstant()));
		register(new ConverterImpl<>(ZonedDateTime.class, LocalDateTime.class, (it, zdt) -> zdt.toLocalDateTime()));
		register(new ConverterImpl<>(LocalDateTime.class, LocalDate.class, (it, ldt) -> ldt.toLocalDate()));
		register(new ConverterImpl<>(LocalDateTime.class, LocalTime.class, (it, ldt) -> ldt.toLocalTime()));
	}

	@NonNull
	public <C extends Converter<?, ?>> C typedRegister(@NonNull C converter) {
		register(converter);
		return converter;
	}

	@Nullable
	public <I, O> Converter<I, O> getConverter(@NonNull Class<I> inputClass, @NonNull Class<O> outputClass) {
		//noinspection unchecked
		return (Converter<I, O>) stream().filter(converter -> {
			if (!converter.getInputClass().isAssignableFrom(inputClass))
				return false;
			return outputClass.isAssignableFrom(converter.getOutputClass());
		}).findAny().orElse(null);
	}

	@Nullable
	public <I, O> Converter<I, O> findConverter(@NonNull Class<I> inputClass, @NonNull Class<O> outputClass) {
		// the goal of this method is basically to find a series of converters that can be chained together
		//   to convert from inputClass to outputClass

		// first, we check if there's a direct converter
		Converter<I, O> direct = getConverter(inputClass, outputClass);
		if (direct != null)
			return direct;

		// if there's no direct converter, we need to find a chain of converters
		List<FindNode> nodes = stream()
				.filter(converter -> converter.getInputClass().isAssignableFrom(inputClass))
				.map(converter -> new FindNode(converter, null))
				.toList();
		while (!nodes.isEmpty()) {
			List<FindNode> newNodes = new ArrayList<>();
			for (FindNode node : nodes) {
				for (Converter<?, ?> converter : this) {
					if (node.isDuplicate(converter.getOutputClass()))
						continue;
					if (!node.converter().getOutputClass().isAssignableFrom(converter.getInputClass()))
						continue;

					FindNode newNode = new FindNode(converter, node);
					if (outputClass.isAssignableFrom(converter.getOutputClass())) {
						// we found a chain! | TODO: cache
						return new ChainConverter<>(inputClass, outputClass, newNode);
					}
					newNodes.add(newNode);
				}
			}
			nodes = newNodes;
		}

		// we couldn't find a chain
		return null;
	}

	private record FindNode(
			@NonNull Converter<?, ?> converter,
			@Nullable FindNode parent
	) {
		/**
		 * Determines if the provided type has already been converted to/from in this chain.
		 *
		 * @return true if the type is a duplicate
		 */
		private boolean isDuplicate(@NonNull Class<?> type) {
			FindNode node = this;
			while (node != null) {
				// TODO: equals or isAssignableFrom?? honestly i'm wondering this about this whole class
				//   I think i've just been using isAssignableFrom for easy conversion from, say, TextChannel to MessageableGuildChannel
				//   But then that creates ambiguous situations like someone asking for just any Channel... it could use either the TextChannel
				//    or VoiceChannel converter, and it's not clear which one should be used
				//   I think it might be better to just use equals and register converters manually for all the main JDA types
				//   but i'm leaving this comment here until i make up my mind
				if (node.converter.getInputClass().equals(type) || node.converter.getOutputClass().equals(type))
					return true;
				node = node.parent;
			}
			return false;
		}
	}

	private static final class ChainConverter<I, O> extends AbstractConverter<I, O> {
		private final List<Converter<?, ?>> converters;

		private ChainConverter(@NonNull Class<I> inputClass, @NonNull Class<O> outputClass, @NonNull FindNode node) {
			// technically the input & output class can be found from the chain, but accepting them as args is a little easier
			//  and is better for type safety
			super(inputClass, outputClass);
			converters = new ArrayList<>();
			while (node != null) {
				converters.add(0, node.converter());
				node = node.parent();
			}
		}

		@SuppressWarnings({"unchecked", "rawtypes"}) // i'm sorry JVM
		@Override
		public @NonNull O convert(@NonNull Interaction interaction, @NonNull I input) {
			Object result = input;
			for (Converter converter : converters)
				result = converter.convert(interaction, result);
			return (O) result;
		}
	}
}
