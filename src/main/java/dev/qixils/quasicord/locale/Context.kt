/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.Interaction
import reactor.core.publisher.Mono
import java.util.*

/**
 * Stores information about the author and location of a message to determine the [Locale]
 * to use for localizing response messages.
 */
interface Context {

    /**
     * Computes the locale to use for localizing a response message.
     *
     * @param localeProvider the locale provider
     * @return the locale to use for localizing a response message
     */
    fun locale(localeProvider: LocaleProvider): Mono<out Locale> {
        return localeProvider.forContext(this)
    }

    // getters
    /**
     * Gets the ID of the user associated with this context.
     *
     * @return discord snowflake or 0 if unspecified
     */
    val user: Long

    /**
     * Gets the locale of the Discord client of the user associated with this context.
     *
     * @return locale or null if unspecified
     */
    val userLocale: DiscordLocale?

    /**
     * Gets the ID of the channel associated with this context.
     *
     * @return discord snowflake or 0 if unspecified
     */
    val channel: Long

    /**
     * Gets the ID of the guild associated with this context.
     *
     * @return discord snowflake or 0 if unspecified
     */
    val guild: Long

    /**
     * Gets the locale set in the settings of the guild associated with this context.
     *
     * @return locale or null if unspecified
     */
    val guildLocale: DiscordLocale?

	companion object {
        /**
         * Creates a context from a [message][Message].
         *
         * @param message the message
         * @return a context
         */
        fun fromMessage(message: Message): Context {
			return ContextImpl(
				user = message.author,
				channel = message.channel,
				guild = if (message.isFromGuild) message.guild else null,
			)
        }

        /**
         * Creates a context from an [interaction][Interaction].
         *
         * @param interaction the interaction
         * @return a context
         */
		@JvmStatic
		fun fromInteraction(interaction: Interaction): Context {
			val channel = interaction.channel
			return ContextImpl(
				user = interaction.user,
				channel = channel as? MessageChannel,
				guild = if (interaction.isFromGuild) interaction.guild else null,
			)
        }

        /**
         * Creates a context from a [channel][MessageChannel].
         *
         * @param channel the channel
         * @return a context
         */
        fun fromChannel(channel: MessageChannel): Context {
			return ContextImpl(
				channel = channel,
				guild = (channel as? GuildMessageChannel)?.guild,
			)
        }

        /**
         * An empty context.
         */
        val EMPTY: Context = ContextImpl()
    }
}
