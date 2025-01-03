/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale

import dev.minn.jda.ktx.util.SLF4J
import dev.minn.jda.ktx.util.SLF4J.getValue
import dev.qixils.quasicord.db.DatabaseManager
import dev.qixils.quasicord.db.collection.LocaleConfig
import dev.qixils.quasicord.extensions.distinct
import dev.qixils.quasicord.locale.Context.Companion.fromInteraction
import dev.qixils.quasicord.locale.LocaleProvider.Companion.instance
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.interactions.Interaction
import org.slf4j.Logger
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

/**
 * Provides the [Locale] selected by a user, channel, guild, or the default one.
 */
open class LocaleProvider
/**
 * Initializes the locale provider with the provided locale and database.
 *
 * @param defaultLocale the default locale to use for [.forContext]
 * if no applicable locale is found
 * @param db            the database to search for locale configurations in
 */private constructor(
	/**
	 * Returns the bot's default [Locale].
	 *
	 * @return the bot's default [Locale]
	 */
	val defaultLocale: Locale,
	private val db: DatabaseManager?,
) {
	// generic
	/**
	 * Returns a [Mono] that emits the [Locale] for the provided object if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param id   the id of the object to get the locale for
	 * @param type the type of the object to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	open suspend fun forObject(id: Long, type: LocaleConfig.EntryType): Locale? {
		if (db == null) return defaultLocale
		return db.cache<LocaleConfig>().getAllByEquals(mapOf("snowflake" to id, "entryType" to type)).singleOrNull()?.language
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided object if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param id   the id of the object to get the locale for
	 * @param type the type of the object to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forObject(id: String, type: LocaleConfig.EntryType): Locale? {
		return forObject(id.toLong(), type)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided object if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param object the object to get the locale for
	 * @param type   the type of the object to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forObject(`object`: ISnowflake, type: LocaleConfig.EntryType): Locale? {
		return forObject(`object`.idLong, type)
	}

	// user
	/**
	 * Returns a [Mono] that emits the [Locale] for the provided user if they have one set,
	 * otherwise it will emit nothing.
	 *
	 * @param userId the id of the user to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forUser(userId: Long): Locale? {
		return forObject(userId, LocaleConfig.EntryType.USER)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided user if they have one set,
	 * otherwise it will emit nothing.
	 *
	 * @param userId the id of the user to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forUser(userId: String): Locale? {
		return forUser(userId.toLong())
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided user if they have one set,
	 * otherwise it will emit nothing.
	 *
	 * @param user the user to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forUser(user: User): Locale? {
		return forUser(user.idLong)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the user from the provided context if they have one set,
	 * otherwise it will emit nothing.
	 *
	 * @param context the context to get the user from
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forUser(context: Context): Locale? {
		return if (context.user == 0L) null else forUser(context.user)
	}

	// channel
	/**
	 * Returns a [Mono] that emits the [Locale] for the provided channel if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param channelId the id of the channel to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forChannel(channelId: Long): Locale? {
		return forObject(channelId, LocaleConfig.EntryType.CHANNEL)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided channel if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param channelId the id of the channel to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forChannel(channelId: String): Locale? {
		return forChannel(channelId.toLong())
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided channel if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param channel the channel to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forChannel(channel: MessageChannel): Locale? {
		return forChannel(channel.idLong)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the channel from the provided context if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param context the context to get the channel from
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forChannel(context: Context): Locale? {
		return if (context.channel == 0L) null else forChannel(context.channel)
	}

	// guild
	/**
	 * Returns a [Mono] that emits the [Locale] for the provided guild if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param guildId the id of the guild to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forGuild(guildId: Long): Locale? {
		return forObject(guildId, LocaleConfig.EntryType.GUILD)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided guild if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param guildId the id of the guild to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forGuild(guildId: String): Locale? {
		return forGuild(guildId.toLong())
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided guild if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param guild the guild to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forGuild(guild: Guild): Locale? {
		return forGuild(guild.idLong)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the guild from the provided context if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param context the context to get the guild from
	 * @return a [Mono] that may emit a [Locale]
	 */
	suspend fun forGuild(context: Context): Locale? {
		return if (context.guild == 0L) null else forGuild(context.guild)
	}

	// misc
	/**
	 * Returns a [Mono] that will emit the [Locale] corresponding to the given [Context].
	 * This searches for a configured [Locale] in the following order:
	 *
	 *  * User
	 *  * User (via Discord)
	 *  * Channel
	 *  * Guild
	 *  * Guild (via Discord)
	 *  * [Default][defaultLocale]
	 *
	 *
	 * @param context the [Context] to get the [Locale] for
	 * @return a [Mono] that will emit the [Locale] corresponding to the given [Context]
	 */
	suspend fun forContext(context: Context): Locale {
		return forUser(context)
			?: context.userLocale?.toLocale()
			?: forChannel(context)
			?: forGuild(context)
			?: context.guildLocale?.toLocale()
			?: defaultLocale
	}

	/**
	 * Returns a [Mono] that will emit the [Locale] corresponding to the given [Interaction].
	 * This searches for a configured [Locale] in the following order:
	 *
	 *  * User
	 *  * User (via Discord)
	 *  * Channel
	 *  * Guild
	 *  * Guild (via Discord)
	 *  * [Default][.defaultLocale]
	 *
	 *
	 * @param interaction the [Interaction] to get the [Locale] for
	 * @return a [Mono] that will emit the [Locale] corresponding to the given [Interaction]
	 */
	suspend fun forInteraction(interaction: Interaction): Locale {
		return forContext(fromInteraction(interaction))
	}

	/**
	 * Returns a [Flux] that will emit all [Locale]s that are configured for the given [Context].
	 *
	 * @param context the [Context] to get the [Locale]s for
	 * @return a [Flux] that will emit all [Locale]s that are configured for the given [Context]
	 */
	fun allForContext(context: Context): Flow<Locale> {
		return flow {
			coroutineScope {
				val user = async { forUser(context) }
				val userLocale = async { context.userLocale?.toLocale() }
				val channel = async { forChannel(context) }
				val guild = async { forGuild(context) }
				val guildLocale = async { context.guildLocale?.toLocale() }
				flowOf(user, userLocale, channel, guild, guildLocale)
					.mapNotNull { it.await() }
					.distinct()
					.collect { emit(it) }
			}
		}
	}

	companion object {
		private val logger: Logger by SLF4J
		private val DUMMY_INSTANCE: LocaleProvider = LocaleProvider(Locale.ROOT, null)
		private var INSTANCE: LocaleProvider = DUMMY_INSTANCE

		// instance management
		var instance: LocaleProvider
			/**
			 * Returns the global locale provider instance.
			 *
			 * @return locale provider instance
			 */
			get() {
				if (INSTANCE === DUMMY_INSTANCE) logger.warn(
					"Locale provider has not been initialized. Using dummy instance."
				)
				return INSTANCE
			}
			/**
			 * Sets the global locale provider instance.
			 *
			 * @param instance locale provider instance
			 */
			set(instance) {
				INSTANCE = instance
			}

		/**
		 * Initializes the locale provider with the provided locale and database.
		 *
		 * @param defaultLocale the default locale to use for [LocaleProvider.forContext]
		 * if no applicable locale is found
		 * @param db            the database to search for locale configurations in
		 */
		fun create(defaultLocale: Locale, db: DatabaseManager): LocaleProvider = LocaleProvider(defaultLocale, db)
	}
}
