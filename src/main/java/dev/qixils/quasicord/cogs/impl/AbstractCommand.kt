/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.cogs.impl

import dev.qixils.quasicord.cogs.Command
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

abstract class AbstractCommand<I : GenericCommandInteractionEvent>(
    override val commandData: CommandData?,
	override val interactionClass: Class<I>,
	override val guildId: String?
) : Command<I>
