/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.cogs

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

/**
 * A wrapper for [SlashCommandData][net.dv8tion.jda.api.interactions.commands.build.SlashCommandData]
 * and the child subcommand classes.
 */
interface SlashCommand : Command<SlashCommandInteractionEvent> {

    /**
     * Gets the branch of this command on the command tree.
     *
     * @return command branch
     */
    val branch: SlashCommandDataBranch
}
