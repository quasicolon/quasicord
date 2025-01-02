/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.cogs.impl

import dev.qixils.quasicord.cogs.SlashCommandDataBranch
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

@JvmRecord
data class SlashCommandDataBranchImpl(
    override val root: SlashCommandData,
	override val group: SubcommandGroupData?,
	override val subcommand: SubcommandData?
) : SlashCommandDataBranch
