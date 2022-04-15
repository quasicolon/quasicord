package dev.qixils.quasicolon.locale;

import dev.qixils.quasicolon.locale.impl.ImmutableContextImpl;
import dev.qixils.quasicolon.locale.impl.MutableContextImpl;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Objects;

/**
 * Stores information about the author and location of a message to determine the {@link Locale}
 * to use for localizing response messages.
 */
// TODO: discord now exposes the user's selected locale in the slash command event object.
//  this should be checked after the user config check but before the channel config check.
public interface Context {

	/**
	 * Computes the locale to use for localizing a response message.
	 *
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

	// copy

	/**
	 * Creates a mutable copy of this context.
	 *
	 * @return a mutable copy of this context
	 */
	default @NonNull Context mutableCopy() {
		return new MutableContextImpl(user(), channel(), guild());
	}

	/**
	 * Creates an immutable copy of this context.
	 *
	 * @return an immutable copy of this context
	 */
	default @NonNull Context immutableCopy() {
		return new ImmutableContextImpl(user(), channel(), guild());
	}

	/**
	 * Creates a context from a {@link Message message}.
	 *
	 * @param message the message
	 * @return a context
	 */
	static @NonNull Context fromMessage(@NonNull Message message) {
		return new ImmutableContextImpl(message.getAuthor().getIdLong(), message.getTextChannel().getIdLong(), message.getGuild().getIdLong());
	}

	/**
	 * An empty context.
	 */
	@NonNull Context EMPTY = new ImmutableContextImpl(0, 0, 0);
}
