/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale

import dev.qixils.quasicord.db.DatabaseManager
import dev.qixils.quasicord.db.collection.LocaleConfig
import dev.qixils.quasicord.locale.Context.Companion.fromInteraction
import dev.qixils.quasicord.locale.LocaleProvider.Companion.instance
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.Interaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
 */(
	/**
	 * Returns the bot's default [Locale].
	 *
	 * @return the bot's default [Locale]
	 */
	val defaultLocale: Locale,
	private val db: DatabaseManager,
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
	open fun forObject(id: Long, type: LocaleConfig.EntryType): Mono<Locale> {
		return Mono.from(
			db.getAllByEquals(
				mapOf(
					"id" to id,
					"entryType" to type,
				), LocaleConfig::class.java
			)
		).map { config -> Locale.forLanguageTag(config.languageCode) }
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided object if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param id   the id of the object to get the locale for
	 * @param type the type of the object to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	fun forObject(id: String, type: LocaleConfig.EntryType): Mono<Locale> {
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
	fun forObject(`object`: ISnowflake, type: LocaleConfig.EntryType): Mono<Locale> {
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
	fun forUser(userId: Long): Mono<Locale> {
		return forObject(userId, LocaleConfig.EntryType.USER)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided user if they have one set,
	 * otherwise it will emit nothing.
	 *
	 * @param userId the id of the user to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	fun forUser(userId: String): Mono<Locale> {
		return forUser(userId.toLong())
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided user if they have one set,
	 * otherwise it will emit nothing.
	 *
	 * @param user the user to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	fun forUser(user: User): Mono<Locale> {
		return forUser(user.idLong)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the user from the provided context if they have one set,
	 * otherwise it will emit nothing.
	 *
	 * @param context the context to get the user from
	 * @return a [Mono] that may emit a [Locale]
	 */
	fun forUser(context: Context): Mono<Locale> {
		return if (context.user == 0L) Mono.empty<Locale>() else forUser(context.user)
	}

	// channel
	/**
	 * Returns a [Mono] that emits the [Locale] for the provided channel if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param channelId the id of the channel to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	fun forChannel(channelId: Long): Mono<Locale> {
		return forObject(channelId, LocaleConfig.EntryType.CHANNEL)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided channel if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param channelId the id of the channel to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	fun forChannel(channelId: String): Mono<Locale> {
		return forChannel(channelId.toLong())
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided channel if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param channel the channel to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	fun forChannel(channel: MessageChannel): Mono<Locale> {
		return forChannel(channel.idLong)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the channel from the provided context if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param context the context to get the channel from
	 * @return a [Mono] that may emit a [Locale]
	 */
	fun forChannel(context: Context): Mono<Locale> {
		return if (context.channel == 0L) Mono.empty<Locale>() else forChannel(context.channel)
	}

	// guild
	/**
	 * Returns a [Mono] that emits the [Locale] for the provided guild if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param guildId the id of the guild to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	fun forGuild(guildId: Long): Mono<Locale> {
		return forObject(guildId, LocaleConfig.EntryType.GUILD)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided guild if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param guildId the id of the guild to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	fun forGuild(guildId: String): Mono<Locale> {
		return forGuild(guildId.toLong())
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the provided guild if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param guild the guild to get the locale for
	 * @return a [Mono] that may emit a [Locale]
	 */
	fun forGuild(guild: Guild): Mono<Locale> {
		return forGuild(guild.idLong)
	}

	/**
	 * Returns a [Mono] that emits the [Locale] for the guild from the provided context if it has one set,
	 * otherwise it will emit nothing.
	 *
	 * @param context the context to get the guild from
	 * @return a [Mono] that may emit a [Locale]
	 */
	fun forGuild(context: Context): Mono<Locale> {
		return if (context.guild == 0L) Mono.empty() else forGuild(context.guild)
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
	fun forContext(context: Context): Mono<Locale> {
		val user = forUser(context)
		val userLocale = Mono.justOrEmpty<DiscordLocale>(context.userLocale)
			.map { locale -> Locale.forLanguageTag(locale.locale) }
		val channel = forChannel(context)
		val guild = forGuild(context)
		val guildLocale = Mono.justOrEmpty<DiscordLocale>(context.guildLocale)
			.map { locale: DiscordLocale -> Locale.forLanguageTag(locale.locale) }
		val defaultLocale = Mono.just(defaultLocale)
		return user
			.switchIfEmpty(userLocale)
			.switchIfEmpty(channel)
			.switchIfEmpty(guild)
			.switchIfEmpty(guildLocale)
			.switchIfEmpty(defaultLocale)
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
	fun forInteraction(interaction: Interaction): Mono<Locale> {
		return forContext(fromInteraction(interaction))
	}

	/**
	 * Returns a [Flux] that will emit all [Locale]s that are configured for the given [Context].
	 *
	 * @param context the [Context] to get the [Locale]s for
	 * @return a [Flux] that will emit all [Locale]s that are configured for the given [Context]
	 */
	fun allForContext(context: Context): Flux<Locale> {
		val user = forUser(context)
		val userLocale = Mono.justOrEmpty<DiscordLocale>(context.userLocale)
			.map { locale -> Locale.forLanguageTag(locale.locale) }
		val channel = forChannel(context)
		val guild = forGuild(context)
		val guildLocale = Mono.justOrEmpty<DiscordLocale>(context.guildLocale)
			.map { locale -> Locale.forLanguageTag(locale.locale) }
		val defaultLocale = Mono.just(defaultLocale)
		return Flux.concat(user, userLocale, channel, guild, guildLocale, defaultLocale).distinct()
	}

	private class DummyLocaleProvider(defaultLocale: Locale) : LocaleProvider(defaultLocale, null as DatabaseManager) {
		override fun forObject(id: Long, type: LocaleConfig.EntryType): Mono<Locale> {
			return Mono.just(defaultLocale)
		}
	}

	companion object {
		// TODO: cache in RAM (can be forever while the bot is still small and then we can add a timeout later)
		private val logger: Logger = LoggerFactory.getLogger(LocaleProvider::class.java)
		private val DUMMY_INSTANCE: LocaleProvider = DummyLocaleProvider(Locale.ROOT)
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
	}
}
