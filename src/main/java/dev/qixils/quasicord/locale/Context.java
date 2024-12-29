/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.locale;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.Interaction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Objects;

/**
 * Stores information about the author and location of a message to determine the {@link Locale}
 * to use for localizing response messages.
 */
public interface Context {

	/**
	 * Computes the locale to use for localizing a response message.
	 *
	 * @param localeProvider the locale provider
	 * @return the locale to use for localizing a response message
	 */
	default @NonNull Mono<Locale> locale(@NonNull LocaleProvider localeProvider) {
		return localeProvider.forContext(this);
	}

	// getters

	/**
	 * Gets the ID of the user associated with this context.
	 *
	 * @return discord snowflake or 0 if unspecified
	 */
	long user();

	/**
	 * Gets the locale of the Discord client of the user associated with this context.
	 *
	 * @return locale or null if unspecified
	 */
	@Nullable DiscordLocale userLocale();

	/**
	 * Gets the ID of the channel associated with this context.
	 *
	 * @return discord snowflake or 0 if unspecified
	 */
	long channel();

	/**
	 * Gets the ID of the guild associated with this context.
	 *
	 * @return discord snowflake or 0 if unspecified
	 */
	long guild();

	/**
	 * Gets the locale set in the settings of the guild associated with this context.
	 *
	 * @return locale or null if unspecified
	 */
	@Nullable DiscordLocale guildLocale();

	// copy

	/**
	 * Creates a {@link Builder} from this context.
	 *
	 * @return a builder from this context
	 */
	default @NonNull Context toBuilder() {
		return builder().user(user()).userLocale(userLocale()).channel(channel()).guild(guild()).guildLocale(guildLocale()).build();
	}

	/**
	 * Creates a new {@link Builder}.
	 *
	 * @return new builder
	 */
	static @NonNull Builder builder() {
		return new ContextBuilderImpl();
	}

	/**
	 * Creates a context from a {@link Message message}.
	 *
	 * @param message the message
	 * @return a context
	 */
	static @NonNull Context fromMessage(@NonNull Message message) {
		Builder context = builder()
				.user(message.getAuthor())
				.channel(message.getChannel());
		if (message.isFromGuild())
			context.guild(message.getGuild()).guildLocale(message.getGuild());
		return context.build();
	}

	/**
	 * Creates a context from an {@link Interaction interaction}.
	 *
	 * @param interaction the interaction
	 * @return a context
	 */
	static @NonNull Context fromInteraction(@NonNull Interaction interaction) {
		Builder context = new ContextBuilderImpl().user(interaction.getUser());
		if (interaction.getChannel() instanceof MessageChannel channel)
			context.channel(channel);
		if (interaction.getGuild() != null)
			context.guild(interaction.getGuild()).guildLocale(interaction.getGuild());
		return context.build();
	}

	/**
	 * Creates a context from a {@link Channel channel}.
	 *
	 * @param channel the channel
	 * @return a context
	 */
	static @NonNull Context fromChannel(@NonNull MessageChannel channel) {
		Builder context = builder()
			.channel(channel);
		if (channel instanceof GuildMessageChannel guildChannel)
			context.guild(guildChannel.getGuild()).guildLocale(guildChannel.getGuild());
		return context.build();
	}

	/**
	 * An empty context.
	 */
	@NonNull Context EMPTY = builder().build();

	/**
	 * Builder for a {@link Context}.
	 */
	@SuppressWarnings("UnusedReturnValue")
	interface Builder {

		// user setter

		/**
		 * Sets the user ID of this context.
		 *
		 * @param user the user ID
		 * @return this context
		 */
		@NonNull Builder user(long user);

		/**
		 * Sets the user ID of this context.
		 *
		 * @param user the user
		 * @return this context
		 */
		default @NonNull Builder user(@NonNull User user) {
			return user(Objects.requireNonNull(user, "user").getIdLong());
		}

		/**
		 * Sets the user ID of this context.
		 *
		 * @param member the member
		 * @return this context
		 */
		default @NonNull Builder user(@NonNull Member member) {
			return user(Objects.requireNonNull(member, "member").getIdLong());
		}

		// user locale setter

		/**
		 * Sets the user locale of this context.
		 *
		 * @param locale the locale
		 * @return this context
		 */
		@NonNull Builder userLocale(@Nullable DiscordLocale locale);

		/**
		 * Sets the user locale of this context.
		 *
		 * @param interaction an interaction triggered by the user
		 * @return this context
		 */
		default @NonNull Builder userLocale(@NonNull Interaction interaction) {
			return userLocale(Objects.requireNonNull(interaction, "interaction").getUserLocale());
		}

		// channel setter

		/**
		 * Sets the channel ID of this context.
		 *
		 * @param channel the channel ID
		 * @return this context
		 */
		@NonNull Builder channel(long channel);

		/**
		 * Sets the channel ID of this context.
		 *
		 * @param channel the channel
		 * @return this context
		 */
		default @NonNull Builder channel(@NonNull MessageChannel channel) {
			return channel(Objects.requireNonNull(channel, "channel").getIdLong());
		}

		// guild setter

		/**
		 * Sets the guild ID of this context.
		 *
		 * @param guild the guild ID
		 * @return this context
		 */
		@NonNull Builder guild(long guild);

		/**
		 * Sets the guild ID of this context.
		 *
		 * @param guild the guild
		 * @return this context
		 */
		default @NonNull Builder guild(@NonNull Guild guild) {
			return guild(Objects.requireNonNull(guild, "guild").getIdLong());
		}

		// guild locale setter

		/**
		 * Sets the guild locale of this context.
		 *
		 * @param locale the locale
		 * @return this context
		 */
		@NonNull Builder guildLocale(@Nullable DiscordLocale locale);

		/**
		 * Sets the guild locale of this context.
		 *
		 * @param guild the guild
		 * @return this context
		 */
		default @NonNull Builder guildLocale(@NonNull Guild guild) {
			Objects.requireNonNull(guild, "guild");
			DiscordLocale locale = guild.getFeatures().contains("COMMUNITY")
				? guild.getLocale()
				: null;
			return guildLocale(locale);
		}

		/**
		 * Builds the {@link Context}.
		 */
		@NonNull Context build();
	}
}
