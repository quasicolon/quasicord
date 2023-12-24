/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.converter;

import dev.qixils.quasicord.Quasicord;
import dev.qixils.quasicord.converter.impl.DurationConverter;
import dev.qixils.quasicord.converter.impl.LocaleConverter;
import dev.qixils.quasicord.converter.impl.ZoneIdConverter;
import dev.qixils.quasicord.converter.impl.ZonedDateTimeConverter;
import dev.qixils.quasicord.locale.Context;
import dev.qixils.quasicord.registry.impl.RegistryImpl;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.Interaction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static dev.qixils.quasicord.converter.ConverterImpl.identity;

public final class ConverterRegistry extends RegistryImpl<Converter<?, ?>> {

	public final Converter<String, ZonedDateTime> ZONED_DATE_TIME;

	public ConverterRegistry(@NonNull Quasicord library) {
		super("converters");
		// contextual converters
		register(new VoidConverterImpl<>(Member.class, Interaction::getMember));
		register(new VoidConverterImpl<>(User.class, Interaction::getUser));
		register(new VoidConverterImpl<>(Guild.class, Interaction::getGuild));
		register(new VoidConverterImpl<>(Channel.class, Interaction::getChannel));
		register(new VoidConverterImpl<>(ChannelType.class, Interaction::getChannelType));
		register(new VoidConverterImpl<>(Context.class, Context::fromInteraction));
		register(new VoidConverterImpl<>(DiscordLocale.class, Interaction::getUserLocale));
		register(new ConverterImpl<>(Context.class, Locale.class, (it, ctx) -> ctx.locale(library.getLocaleProvider()).block())); // TODO: async
		register(new LocaleConverter(library));
		register(new ConverterImpl<>(Locale.class, DiscordLocale.class, (it, locale) -> DiscordLocale.from(locale)));
		register(new ConverterImpl<>(DiscordLocale.class, Locale.class, (it, locale) -> locale.toLocale()));
		register(new ZoneIdConverter());
		// channels
		register(ConverterImpl.channel(TextChannel.class));
		register(ConverterImpl.channel(PrivateChannel.class));
		register(ConverterImpl.channel(VoiceChannel.class));
		register(ConverterImpl.channel(Category.class));
		register(ConverterImpl.channel(NewsChannel.class));
		register(ConverterImpl.channel(StageChannel.class));
		register(ConverterImpl.channel(ThreadChannel.class));
		register(ConverterImpl.channel(ForumChannel.class));
		// zoned date time and misc conversions to other java time types
		register(new DurationConverter(library));
		ZONED_DATE_TIME = typedRegister(new ZonedDateTimeConverter(library));
		register(new ConverterImpl<>(ZonedDateTime.class, Instant.class, (it, zdt) -> zdt.toInstant()));
		register(new ConverterImpl<>(ZonedDateTime.class, LocalDateTime.class, (it, zdt) -> zdt.toLocalDateTime()));
		register(new ConverterImpl<>(LocalDateTime.class, LocalDate.class, (it, ldt) -> ldt.toLocalDate()));
		register(new ConverterImpl<>(LocalDateTime.class, LocalTime.class, (it, ldt) -> ldt.toLocalTime()));
		// numbers
		register(new ConverterImpl<>(Number.class, Integer.class, (it, l) -> l.intValue()));
		register(new ConverterImpl<>(Number.class, Long.class, (it, d) -> d.longValue()));
		register(new ConverterImpl<>(Number.class, Float.class, (it, f) -> f.floatValue()));
		register(new ConverterImpl<>(Number.class, Double.class, (it, d) -> d.doubleValue()));
		register(new ConverterImpl<>(Number.class, Short.class, (it, s) -> s.shortValue()));
		register(new ConverterImpl<>(Number.class, Byte.class, (it, b) -> b.byteValue()));
		// misc
		register(new ConverterImpl<>(User.class, Member.class, (it, u) -> Objects.requireNonNull(Objects.requireNonNull(it.getGuild()).getMember(u))));
	}

	@NonNull
	public <C extends Converter<?, ?>> C typedRegister(@NonNull C converter) {
		register(converter);
		return converter;
	}

	@Nullable
	public <I, O> Converter<I, O> getConverter(@NonNull Class<I> inputClass, @NonNull Class<O> outputClass) {
		if (inputClass == outputClass)
			//noinspection unchecked
			return (Converter<I, O>) identity(outputClass);

		//noinspection unchecked
		return (Converter<I, O>) stream().filter(converter ->
				converter.getInputClass() == inputClass && converter.getOutputClass() == outputClass
		).findAny().or(() -> stream().filter(converter ->
				converter.getInputClass().isAssignableFrom(inputClass) && outputClass.isAssignableFrom(converter.getOutputClass())
		).findAny()).orElse(null);
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
		List<Converter<?, ?>> encounteredConverters = new ArrayList<>();
		List<FindNode> nodes = stream()
				.filter(converter -> converter.getInputClass().isAssignableFrom(inputClass))
				.map(converter -> new FindNode(converter, null))
				.toList();
		while (!nodes.isEmpty()) {
			List<FindNode> newNodes = new ArrayList<>();
			for (FindNode node : nodes) {
				for (Converter<?, ?> converter : this) {
					if (!converter.canConvertTo())
						continue;
					if (encounteredConverters.contains(converter))
						continue;
					if (!node.converter().getOutputClass().isAssignableFrom(converter.getInputClass()))
						continue;
					if (node.isDuplicate(converter.getOutputClass()))
						continue;

					FindNode newNode = new FindNode(converter, node);
					if (outputClass.isAssignableFrom(converter.getOutputClass())) {
						// we found a chain! | TODO: cache
						return new ChainConverter<>(inputClass, outputClass, newNode);
					}
					if (converter.canConvertFrom())
						newNodes.add(newNode);
					encounteredConverters.add(converter);
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
