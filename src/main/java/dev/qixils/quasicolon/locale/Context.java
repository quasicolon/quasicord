/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.locale;

import dev.qixils.quasicolon.locale.impl.ImmutableContextImpl;
import dev.qixils.quasicolon.locale.impl.MutableContextImpl;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
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
// TODO: discord now exposes the user's selected locale in the slash command event object.
//  this should be checked after the user config check but before the channel config check.
// TODO: discord also exposes the guild's locale! that should be checked after the guild config check.
// TODO: the interface should not be pseudo-mutable... it should be immutable with a mutable builder.
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

	/**
	 * Determines if this context is mutable.
	 *
	 * @return true if this context is mutable
	 */
	boolean isMutable();

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

	// user setter

	/**
	 * Sets the user ID of this context.
	 *
	 * @param user the user ID
	 * @return this context
	 * @throws UnsupportedOperationException if this context is immutable
	 */
	@NonNull Context user(long user);

	/**
	 * Sets the user ID of this context.
	 *
	 * @param user the user
	 * @return this context
	 * @throws UnsupportedOperationException if this context is immutable
	 */
	default @NonNull Context user(@NonNull User user) {
		return user(Objects.requireNonNull(user, "user").getIdLong());
	}

	/**
	 * Sets the user ID of this context.
	 *
	 * @param member the member
	 * @return this context
	 * @throws UnsupportedOperationException if this context is immutable
	 */
	default @NonNull Context user(@NonNull Member member) {
		return user(Objects.requireNonNull(member, "member").getIdLong());
	}

	// user locale setter

	/**
	 * Sets the user locale of this context.
	 *
	 * @param locale the locale
	 * @return this context
	 * @throws UnsupportedOperationException if this context is immutable
	 */
	@NonNull Context userLocale(@Nullable DiscordLocale locale);

	/**
	 * Sets the user locale of this context.
	 *
	 * @param interaction an interaction triggered by the user
	 * @return this context
	 * @throws UnsupportedOperationException if this context is immutable
	 */
	default @NonNull Context userLocale(@NonNull Interaction interaction) {
		return userLocale(Objects.requireNonNull(interaction, "interaction").getUserLocale());
	}

	// channel setter

	/**
	 * Sets the channel ID of this context.
	 *
	 * @param channel the channel ID
	 * @return this context
	 * @throws UnsupportedOperationException if this context is immutable
	 */
	@NonNull Context channel(long channel);

	/**
	 * Sets the channel ID of this context.
	 *
	 * @param channel the channel
	 * @return this context
	 * @throws UnsupportedOperationException if this context is immutable
	 */
	default @NonNull Context channel(@NonNull MessageChannel channel) {
		return channel(Objects.requireNonNull(channel, "channel").getIdLong());
	}

	// guild setter

	/**
	 * Sets the guild ID of this context.
	 *
	 * @param guild the guild ID
	 * @return this context
	 * @throws UnsupportedOperationException if this context is immutable
	 */
	@NonNull Context guild(long guild);

	/**
	 * Sets the guild ID of this context.
	 *
	 * @param guild the guild
	 * @return this context
	 * @throws UnsupportedOperationException if this context is immutable
	 */
	default @NonNull Context guild(@NonNull Guild guild) {
		return guild(Objects.requireNonNull(guild, "guild").getIdLong());
	}

	// guild locale setter

	/**
	 * Sets the guild locale of this context.
	 *
	 * @param locale the locale
	 * @return this context
	 * @throws UnsupportedOperationException if this context is immutable
	 */
	@NonNull Context guildLocale(@Nullable DiscordLocale locale);

	/**
	 * Sets the guild locale of this context.
	 *
	 * @param guild the guild
	 * @return this context
	 * @throws UnsupportedOperationException if this context is immutable
	 */
	default @NonNull Context guildLocale(@NonNull Guild guild) {
		Objects.requireNonNull(guild, "guild");
		DiscordLocale locale = guild.getFeatures().contains("COMMUNITY")
				? guild.getLocale()
				: null;
		return guildLocale(locale);
	}

	// copy

	/**
	 * Creates a mutable copy of this context.
	 *
	 * @return a mutable copy of this context
	 */
	default @NonNull Context mutableCopy() {
		return new MutableContextImpl(user(), userLocale(), channel(), guild(), guildLocale());
	}

	/**
	 * Creates an immutable copy of this context.
	 *
	 * @return an immutable copy of this context
	 */
	default @NonNull Context immutableCopy() {
		return new ImmutableContextImpl(user(), userLocale(), channel(), guild(), guildLocale());
	}

	/**
	 * Creates a context from a {@link Message message}.
	 *
	 * @param message the message
	 * @return a context
	 */
	static @NonNull Context fromMessage(@NonNull Message message) {
		Context context = new MutableContextImpl()
				.user(message.getAuthor())
				.channel(message.getChannel());
		if (message.isFromGuild())
			context.guild(message.getGuild()).guildLocale(message.getGuild());
		return context.immutableCopy();
	}

	/**
	 * Creates a context from an {@link Interaction interaction}.
	 *
	 * @param interaction the interaction
	 * @return a context
	 */
	static @NonNull Context fromInteraction(@NonNull Interaction interaction) {
		Context context = new MutableContextImpl().user(interaction.getUser());
		if (interaction.getChannel() instanceof MessageChannel channel)
			context.channel(channel);
		if (interaction.getGuild() != null)
			context.guild(interaction.getGuild()).guildLocale(interaction.getGuild());
		return context.immutableCopy();
	}

	/**
	 * An empty context.
	 */
	@NonNull Context EMPTY = new ImmutableContextImpl(0, null, 0, 0, null);
}
