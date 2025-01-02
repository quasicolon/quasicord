/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.cogs

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

/**
 * Represents a branch of the command tree.
 * Describes the root command, and if respectively present, the subcommand and subcommand group.
 */
interface SlashCommandDataBranch {
    /**
     * The root command.
     *
     * @return the root command
     */
    val root: SlashCommandData

    /**
     * The subcommand group, if present.
     *
     * @return the subcommand group
     */
    val group: SubcommandGroupData?

    /**
     * The subcommand, if present.
     *
     * @return the subcommand
     */
	val subcommand: SubcommandData?

    /**
     * The full name of this command according to Discord.
     *
     * @return the command name
     */
	val name: String get() {
        var id = root.name

        val group = this.group
        if (group != null) id += ' '.toString() + group.name

        val subcommand = this.subcommand
        if (subcommand != null) id += ' '.toString() + subcommand.name

        return id
    }

    /**
     * Returns the root command if this is a standalone command.
     *
     * @return the root command or null
     */
	val rootIfStandalone: SlashCommandData? get() {
        if (subcommand != null) return null
        return root
    }
}
