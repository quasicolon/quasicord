/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.registry.core;

import dev.qixils.quasicolon.Quasicord;
import dev.qixils.quasicolon.cogs.Command;
import dev.qixils.quasicolon.cogs.GlobalCog;
import dev.qixils.quasicolon.registry.impl.ClosableRegistryImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry of {@link GlobalCog}s.
 */
public class GlobalCogRegistry extends ClosableRegistryImpl<GlobalCog> {
	private final @NonNull Quasicord quasicord;

	public GlobalCogRegistry(@NonNull Quasicord quasicord) {
		super("cogs", true);
		this.quasicord = quasicord;
	}

	@Override
	public void close() {
		super.close();
		List<Command<?>> commands = new ArrayList<>();
		for (GlobalCog cog : this) {
			try {
				cog.onLoad();
				commands.addAll(cog.getCommands());
			} catch (Exception e) {
				quasicord.getLogger().error("Failed to load cog " + cog.getClass().getName(), e);
				// TODO: undo loading?
			}
		}
		quasicord.getJDA().updateCommands()
				.addCommands(commands.stream().map(Command::getCommandData).toList())
				.queue(); // TODO: only update if there are new/updated commands
		// TODO: process command events
	}
}
