package dev.qixils.quasicord.extensions

import dev.qixils.quasicord.locale.Context
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.interactions.Interaction

val Interaction.context: Context
	get() = Context.fromInteraction(this)

val Message.context: Context
	get() = Context.fromMessage(this)

val MessageChannel.context: Context
	get() = Context.fromChannel(this)
