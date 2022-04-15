/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.processors;

import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import cloud.commandframework.execution.postprocessor.CommandPostprocessor;
import cloud.commandframework.jda.JDACommandSender;
import cloud.commandframework.services.types.ConsumerService;
import dev.qixils.quasicolon.Quasicolon;
import net.dv8tion.jda.api.entities.GuildChannel;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

/**
 * Prevents per-guild commands from being executed in the wrong guild.
 */
public final class GuildCommandProcessor implements CommandPostprocessor<JDACommandSender> {

	@Override
	public void accept(@NonNull CommandPostprocessingContext<JDACommandSender> context) {
		Optional<Long> guildID = context.getCommand().getCommandMeta().get(Quasicolon.GUILD_KEY);
		if (guildID.isEmpty()) return;
		JDACommandSender sender = context.getCommandContext().getSender();
		if (!(sender.getChannel() instanceof GuildChannel channel) || channel.getGuild().getIdLong() != guildID.get()) {
			// this command is not available in this guild so do not execute it
			ConsumerService.interrupt();
		}
	}
}
