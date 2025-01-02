/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord

import dev.qixils.quasicord.cogs.Command
import dev.qixils.quasicord.cogs.SlashCommand
import dev.qixils.quasicord.decorators.AnnotationParser
import dev.qixils.quasicord.error.UserError
import dev.qixils.quasicord.locale.Context
import dev.qixils.quasicord.text.Text
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class CommandManager(library: Quasicord) {
    val library: Quasicord
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    protected val commands: MutableMap<String?, MutableMap<String, Command<*>>> = HashMap()
    private val parser: AnnotationParser
    private var initialUpsertDone = false

    init {
        commands[null] = HashMap()
        this.library = library
        this.parser = AnnotationParser(this)
    }

    fun getCommand(discordName: String, guildId: String?): Command<*>? {
        if (guildId != null && commands.containsKey(guildId)) {
            val command = commands[guildId]!![discordName]
            if (command != null) return command
        }
        return commands[null]!![discordName]
    }

    fun upsertCommands(jda: JDA) {
        if (initialUpsertDone) return
        initialUpsertDone = true
        logger.info("Upserting commands")
        for ((guildId, guildCommands) in commands) {
            var updater: CommandListUpdateAction
            if (guildId == null) {
                updater = jda.updateCommands()
            } else {
                val guild = jda.getGuildById(guildId) ?: continue
                updater = guild.updateCommands()
            }

            val rootSlashCommands: MutableSet<String> = HashSet()

            for (command in guildCommands.values) {
                var commandData: CommandData?
                if (command is SlashCommand) {
                    commandData = command.branch.root
                    if (rootSlashCommands.contains(commandData.name)) continue
                    rootSlashCommands.add(commandData.name)
                } else {
                    commandData = command.commandData
                }

                if (commandData == null) continue  // i don't think this should happen but just in case

                logger.debug("Upserting command {} to guild {}", commandData.name, guildId)
                updater.addCommands(commandData)
            }

            updater.queue()
        }
    }

    fun registerCommand(command: Command<*>) {
        val guildId = command.guildId
        commands.computeIfAbsent(guildId) { HashMap() }[command.discordName] =
            command
        if (!initialUpsertDone) return

        val cmd = command.commandData ?: return

        val jda = library.jda
        val upsert: RestAction<net.dv8tion.jda.api.interactions.commands.Command>

        if (guildId == null) {
            upsert = jda.upsertCommand(cmd)
        } else {
            val guild = jda.getGuildById(guildId) ?: return
            upsert = guild.upsertCommand(cmd)
        }

        upsert.queue()
    }

    fun discoverCommands(`object`: Any) {
        parser.parse(`object`).forEach(Consumer { command: Command<*> -> this.registerCommand(command) })
    }

    @SubscribeEvent
    fun onCommandInteraction(event: GenericCommandInteractionEvent) {
        val guildId = if (event.guild == null) event.guild!!.id else null
        val command = getCommand(event.fullCommandName, guildId)
        if (command == null) {
            library.logger.error("Could not find an executor for command {}", event.fullCommandName)
            sendEphemeral(event, Text.single(Key.library("exception.command_error")))
            return
        }
		if (!(command.interactionClass.isAssignableFrom(event.javaClass))) {
			library.logger.error("Invalid event type {} for command {}", event.javaClass.name, event.fullCommandName)
			sendEphemeral(event, Text.single(Key.library("exception.command_error")))
			return
		}
        try {
			@Suppress("UNCHECKED_CAST")
			(command as Command<Any>).accept(event)
		} catch (e: UserError) {
            sendEphemeral(event, e)
        } catch (e: Exception) {
            library.logger.error("Failed to execute command " + event.fullCommandName, e)
            sendEphemeral(event, Text.single(Key.library("exception.command_error")))
        }
    }

    companion object {
        private fun sendEphemeral(event: IReplyCallback, text: Text) {
            text.asString(Context.fromInteraction(event)).subscribe { string: String? ->
                event.reply(
                    string!!
                ).setEphemeral(true).queue()
            }
        }
    }
}
