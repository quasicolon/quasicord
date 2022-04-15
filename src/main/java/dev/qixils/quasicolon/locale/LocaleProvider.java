/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.locale;

import dev.qixils.quasicolon.db.DatabaseManager;
import dev.qixils.quasicolon.locale.LocaleConfig.EntryType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Map;

/**
 * Provides the {@link Locale} selected by a user, channel, guild, or the default one.
 */
public class LocaleProvider {
	private static final @NonNull Logger logger = LoggerFactory.getLogger(LocaleProvider.class);
	private static final @NonNull LocaleProvider DUMMY_INSTANCE = new DummyLocaleProvider(Locale.ROOT);
	private static @NonNull LocaleProvider INSTANCE = DUMMY_INSTANCE;

	private final @NonNull Locale defaultLocale;
	private final @NonNull DatabaseManager db;

	/**
	 * Initializes the locale provider with the provided locale and database.
	 *
	 * @param defaultLocale the default locale to use for {@link #forContext(Context)}
	 *                      if no applicable locale is found
	 * @param db            the database to search for locale configurations in
	 */
	public LocaleProvider(@NonNull Locale defaultLocale, @NonNull DatabaseManager db) {
		this.defaultLocale = defaultLocale;
		this.db = db;
	}

	// instance management

	/**
	 * Returns the global locale provider instance.
	 *
	 * @return locale provider instance
	 */
	public static @NonNull LocaleProvider getInstance() {
		if (INSTANCE == DUMMY_INSTANCE)
			logger.warn("Locale provider has not been initialized. Using dummy instance.");
		return INSTANCE;
	}

	/**
	 * Sets the global locale provider instance.
	 *
	 * @param instance locale provider instance
	 */
	public static void setInstance(@NonNull LocaleProvider instance) {
		INSTANCE = instance;
	}

	// generic

	/**
	 * Returns a {@link Mono} that emits the {@link Locale} for the provided object if it has
	 * one set, otherwise it will emit nothing.
	 *
	 * @param id   the id of the object to get the locale for
	 * @param type the type of the object to get the locale for
	 * @return a {@link Mono} that may emit a {@link Locale}
	 */
	public @NonNull Mono<@NonNull Locale> forObject(long id, @NonNull EntryType type) {
		return Mono.from(db.getAllByEquals(Map.of(
				"id", id,
				"entryType", type
		), LocaleConfig.class)).map(config -> Locale.forLanguageTag(config.getLanguageCode()));
	}

	/**
	 * Returns a {@link Mono} that emits the {@link Locale} for the provided object if it has
	 * one set, otherwise it will emit nothing.
	 *
	 * @param id   the id of the object to get the locale for
	 * @param type the type of the object to get the locale for
	 * @return a {@link Mono} that may emit a {@link Locale}
	 */
	public @NonNull Mono<@NonNull Locale> forObject(@NonNull String id, @NonNull EntryType type) {
		return forObject(Long.parseLong(id), type);
	}

	/**
	 * Returns a {@link Mono} that emits the {@link Locale} for the provided object if it has
	 * one set, otherwise it will emit nothing.
	 *
	 * @param object the object to get the locale for
	 * @param type   the type of the object to get the locale for
	 * @return a {@link Mono} that may emit a {@link Locale}
	 */
	public @NonNull Mono<@NonNull Locale> forObject(@NonNull ISnowflake object, @NonNull EntryType type) {
		return forObject(object.getIdLong(), type);
	}

	// user

	/**
	 * Returns a {@link Mono} that emits the {@link Locale} for the provided user if they have
	 * one set, otherwise it will emit nothing.
	 *
	 * @param userId the id of the user to get the locale for
	 * @return a {@link Mono} that may emit a {@link Locale}
	 */
	public @NonNull Mono<@NonNull Locale> forUser(long userId) {
		return forObject(userId, EntryType.USER);
	}

	/**
	 * Returns a {@link Mono} that emits the {@link Locale} for the provided user if they have
	 * one set, otherwise it will emit nothing.
	 *
	 * @param userId the id of the user to get the locale for
	 * @return a {@link Mono} that may emit a {@link Locale}
	 */
	public @NonNull Mono<@NonNull Locale> forUser(@NonNull String userId) {
		return forUser(Long.parseLong(userId));
	}

	/**
	 * Returns a {@link Mono} that emits the {@link Locale} for the provided user if they have
	 * one set, otherwise it will emit nothing.
	 *
	 * @param user the user to get the locale for
	 * @return a {@link Mono} that may emit a {@link Locale}
	 */
	public @NonNull Mono<@NonNull Locale> forUser(@NonNull User user) {
		return forUser(user.getIdLong());
	}

	// channel

	/**
	 * Returns a {@link Mono} that emits the {@link Locale} for the provided channel if it has
	 * one set, otherwise it will emit nothing.
	 *
	 * @param channelId the id of the channel to get the locale for
	 * @return a {@link Mono} that may emit a {@link Locale}
	 */
	public @NonNull Mono<@NonNull Locale> forChannel(long channelId) {
		return forObject(channelId, EntryType.CHANNEL);
	}

	/**
	 * Returns a {@link Mono} that emits the {@link Locale} for the provided channel if it has
	 * one set, otherwise it will emit nothing.
	 *
	 * @param channelId the id of the channel to get the locale for
	 * @return a {@link Mono} that may emit a {@link Locale}
	 */
	public @NonNull Mono<@NonNull Locale> forChannel(@NonNull String channelId) {
		return forChannel(Long.parseLong(channelId));
	}

	/**
	 * Returns a {@link Mono} that emits the {@link Locale} for the provided channel if it has
	 * one set, otherwise it will emit nothing.
	 *
	 * @param channel the channel to get the locale for
	 * @return a {@link Mono} that may emit a {@link Locale}
	 */
	public @NonNull Mono<@NonNull Locale> forChannel(@NonNull MessageChannel channel) {
		return forChannel(channel.getIdLong());
	}

	// guild

	/**
	 * Returns a {@link Mono} that emits the {@link Locale} for the provided guild if it has
	 * one set, otherwise it will emit nothing.
	 *
	 * @param guildId the id of the guild to get the locale for
	 * @return a {@link Mono} that may emit a {@link Locale}
	 */
	public @NonNull Mono<@NonNull Locale> forGuild(long guildId) {
		return forObject(guildId, EntryType.CHANNEL);
	}

	/**
	 * Returns a {@link Mono} that emits the {@link Locale} for the provided guild if it has
	 * one set, otherwise it will emit nothing.
	 *
	 * @param guildId the id of the guild to get the locale for
	 * @return a {@link Mono} that may emit a {@link Locale}
	 */
	public @NonNull Mono<@NonNull Locale> forGuild(@NonNull String guildId) {
		return forGuild(Long.parseLong(guildId));
	}

	/**
	 * Returns a {@link Mono} that emits the {@link Locale} for the provided guild if it has
	 * one set, otherwise it will emit nothing.
	 *
	 * @param guild the guild to get the locale for
	 * @return a {@link Mono} that may emit a {@link Locale}
	 */
	public @NonNull Mono<@NonNull Locale> forGuild(@NonNull Guild guild) {
		return forGuild(guild.getIdLong());
	}

	// misc

	/**
	 * Returns the bot's default {@link Locale}.
	 *
	 * @return the bot's default {@link Locale}
	 */
	public @NonNull Locale defaultLocale() {
		return defaultLocale;
	}

	/**
	 * Returns a {@link Mono} that will emit the {@link Locale} corresponding to the given
	 * {@link Context}. This searches for a configured {@link Locale} in the following order:
	 * <ul>
	 *     <li>User</li>
	 *     <li>Channel</li>
	 *     <li>Guild</li>
	 *     <li>{@link #defaultLocale() Default}</li>
	 * </ul>
	 *
	 * @param context the {@link Context} to get the {@link Locale} for
	 * @return a {@link Mono} that will emit the {@link Locale} corresponding to the given {@link Context}
	 */
	public @NonNull Mono<Locale> forContext(@NonNull Context context) {
		Mono<Locale> user = context.user() == 0
				? Mono.empty()
				: forUser(context.user());
		Mono<Locale> channel = context.channel() == 0
				? Mono.empty()
				: forChannel(context.channel());
		Mono<Locale> guild = context.guild() == 0
				? Mono.empty()
				: forGuild(context.guild());
		return user.switchIfEmpty(channel).switchIfEmpty(guild).switchIfEmpty(Mono.just(defaultLocale));
	}

	private static final class DummyLocaleProvider extends LocaleProvider {
		@SuppressWarnings("ConstantConditions")
		public DummyLocaleProvider(@NonNull Locale defaultLocale) {
			super(defaultLocale, null);
		}

		@Override
		public @NonNull Mono<Locale> forObject(long id, @NonNull EntryType type) {
			return Mono.just(defaultLocale());
		}
	}
}
