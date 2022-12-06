/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl;

import dev.qixils.quasicolon.Quasicord;
import dev.qixils.quasicolon.cogs.Command;
import dev.qixils.quasicolon.cogs.GuildCog;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;

/**
 * A basic implementation of a cog that applies to only one guild.
 * <p>
 * All associated commands are automatically registered upon construction.
 */
public abstract class AbstractGuildCog extends AbstractCog implements GuildCog {
	protected final long guildId;

	protected AbstractGuildCog(@NonNull Quasicord library, long guildId) {
		super(library);
		this.guildId = guildId;

		// call load method
		onLoad();

		// register commands
		Collection<Command<?>> commands = getCommands();
		getGuild().ifPresentOrElse(
				guild -> guild.updateCommands()
						.addCommands(commands.stream().map(Command::getCommandData).toList())
						.queue(), // TODO: only update if there are new/updated commands
				// TODO: process command events
				() -> library.getLogger().warn("Guild {} not found when loading cog {}", guildId, getClass().getSimpleName())
		);
	}

	@Override
	public void onLoad() {
	}
}
