package dev.qixils.quasicolon.locale;

import dev.qixils.quasicolon.db.DatabaseManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Provides the {@link Locale} selected by a user, channel, guild, or the default one.
 */
public class LocaleProvider {
	private static final @NonNull Logger logger = LoggerFactory.getLogger(LocaleProvider.class);
	private static final @NonNull LocaleProvider DUMMY_INSTANCE = new DummyLocaleProvider(Locale.ENGLISH);
	private static @NonNull LocaleProvider INSTANCE = DUMMY_INSTANCE;

	private final @NonNull Locale defaultLocale;
	private final @NonNull DatabaseManager db;

	// TODO mongodb
	public LocaleProvider(@NonNull Locale defaultLocale, @NonNull DatabaseManager db) {
		this.defaultLocale = defaultLocale;
		this.db = db;
	}

	// instance management | TODO: determine if this is necessary

	public static @NonNull LocaleProvider getInstance() {
		if (INSTANCE == DUMMY_INSTANCE)
			logger.warn("Locale provider has not been initialized. Using dummy instance.");
		return INSTANCE;
	}

	public static void setInstance(@NonNull LocaleProvider instance) {
		INSTANCE = instance;
	}

	// user

	public @Nullable Locale forUser(long userId) {
		// TODO: Implement; use Locale.forLanguageTag() to get the Locale from a language tag
	}

	public @Nullable Locale forUser(@NonNull String userId) {
		return forUser(Long.parseLong(userId));
	}

	public @Nullable Locale forUser(@NonNull User user) {
		return forUser(user.getIdLong());
	}

	// channel

	public @Nullable Locale forChannel(long channelId) {
		// TODO
	}

	public @Nullable Locale forChannel(@NonNull String channelId) {
		return forChannel(Long.parseLong(channelId));
	}

	public @Nullable Locale forChannel(@NonNull MessageChannel channel) {
		return forChannel(channel.getIdLong());
	}

	// guild

	public @Nullable Locale forGuild(long guildId) {
		// TODO
	}

	public @Nullable Locale forGuild(@NonNull String guildId) {
		return forGuild(Long.parseLong(guildId));
	}

	public @Nullable Locale forGuild(@NonNull Guild guild) {
		return forGuild(guild.getIdLong());
	}

	// misc

	public @NonNull Locale defaultLocale() {
		return defaultLocale;
	}

	/**
	 * Returns the {@link Locale} corresponding to the given {@link Context}.
	 * This searches for a configured {@link Locale} in the following order:
	 * <ul>
	 *     <li>User</li>
	 *     <li>Channel</li>
	 *     <li>Guild</li>
	 *     <li>{@link #defaultLocale() Default}</li>
	 * </ul>
	 *
	 * @param context the {@link Context} to get the {@link Locale} for
	 * @return the {@link Locale} corresponding to the given {@link Context}
	 */
	public @NonNull Locale forContext(@NonNull Context context) {
		Locale locale;
		if (context.user() != 0) {
			locale = forUser(context.user());
			if (locale != null)
				return locale;
		}
		if (context.channel() != 0) {
			locale = forChannel(context.channel());
			if (locale != null)
				return locale;
		}
		if (context.guild() != 0) {
			locale = forGuild(context.guild());
			if (locale != null)
				return locale;
		}
		return defaultLocale;
	}

	private static final class DummyLocaleProvider extends LocaleProvider {
		public DummyLocaleProvider(@NonNull Locale defaultLocale) {
			super(defaultLocale);
		}

		@Override
		public @NonNull Locale forUser(long userId) {
			return defaultLocale();
		}

		@Override
		public @NonNull Locale forChannel(long channelId) {
			return defaultLocale();
		}

		@Override
		public @NonNull Locale forGuild(@NonNull Guild guild) {
			return defaultLocale();
		}
	}
}
