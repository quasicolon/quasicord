/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.interactions.DiscordLocale

@JvmRecord
internal data class ContextImpl(
    override val user: Long = 0,
	override val userLocale: DiscordLocale? = null,
	override val channel: Long = 0,
	override val guild: Long = 0,
	override val guildLocale: DiscordLocale? = null,
) : Context {
	constructor(
		user: User? = null,
		userLocale: DiscordLocale? = null,
		channel: Channel? = null,
		guild: Guild? = null,
	) : this(
		user?.idLong ?: 0,
		userLocale,
		channel?.idLong ?: 0,
		guild?.idLong ?: 0,
		if (guild?.features?.contains("COMMUNITY") == true) guild.locale
		else null,
	)
}

