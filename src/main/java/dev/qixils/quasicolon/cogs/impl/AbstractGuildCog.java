/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.jda.JDACommandSender;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.meta.SimpleCommandMeta;
import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.cogs.GuildCog;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A basic implementation of a cog that applies to only one guild.
 * <p>
 * All associated commands are automatically registered upon construction.
 */
public abstract class AbstractGuildCog extends AbstractCog implements GuildCog {
	protected final long guildId;

	protected AbstractGuildCog(@NonNull Quasicolon library, long guildId) {
		super(library);
		this.guildId = guildId;
		// call load method
		onLoad();
		// register commands
		getGuild().ifPresentOrElse(
				guild -> guild.updateCommands().addCommands(getApplicationCommands()).queue(), // TODO: only update if there are new/updated commands
				() -> library.getLogger().warn("Guild {} not found when loading cog {}", guildId, getClass().getSimpleName())
		);
		getCustomCommands().forEach(command -> library.getCommandManager().command(command));
	}

	@Override
	protected @NonNull CommandMeta metaMapper(@NonNull ParserParameters parameters) {
		// add guild key to @CommandMethod-based commands
		return SimpleCommandMeta.builder()
				.with(super.metaMapper(parameters))
				.with(Quasicolon.GUILD_KEY, guildId)
				.build();
	}

	// override the addCustomCommand method to add the guild key to the command
	@Override
	protected void addCustomCommand(Command.@NonNull Builder<JDACommandSender> command) {
		super.addCustomCommand(command.meta(Quasicolon.GUILD_KEY, guildId));
	}
}
