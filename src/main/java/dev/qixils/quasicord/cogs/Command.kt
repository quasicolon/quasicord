/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.cogs

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

/**
 * A wrapper for [CommandData] which defines an executor for the command.
 */
interface Command<I : GenericCommandInteractionEvent> {
    /**
     * Gets the full name of the command.
     *
     * @return command name
     */
	val name: String

    /**
     * Gets the full name of the command according to JDA.
     *
     * @return JDA command name
     */
    val discordName: String

    /**
     * Gets the guild that this command should be created in.
     * Global commands return null.
     *
     * @return guild id or null
     */
    val guildId: String?

    /**
     * Gets the data (i.e. the defined arguments) for the command.
     * May be null for subcommands.
     *
     * @return command data
     */
    val commandData: CommandData?

    /**
     * Gets the class of the [GenericCommandInteractionEvent] object which the executor will use.
     *
     * @return class of the expected [GenericCommandInteractionEvent] object
     */
    val interactionClass: Class<I>

    /**
     * Executes the command.
     *
     * @param interaction the interaction object which triggered the command execution
     */
    fun accept(interaction: I)
}
