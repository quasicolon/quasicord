/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.registry.core;

import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.cogs.GlobalCog;
import dev.qixils.quasicolon.registry.impl.ClosableRegistryImpl;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry of {@link GlobalCog}s.
 */
public class GlobalCogRegistry extends ClosableRegistryImpl<GlobalCog> {
	private final @NonNull Quasicolon quasicolon;

	public GlobalCogRegistry(@NonNull Quasicolon quasicolon) {
		super("cogs", true);
		this.quasicolon = quasicolon;
	}

	@Override
	public void close() {
		super.close();
		List<CommandData> applicationCommands = new ArrayList<>();
		for (GlobalCog cog : this) {
			try {
				cog.onLoad();
				applicationCommands.addAll(cog.getApplicationCommands());
				cog.getCustomCommands().forEach(command -> quasicolon.getCommandManager().command(command));
			} catch (Exception e) {
				quasicolon.getLogger().error("Failed to load cog " + cog.getClass().getName(), e);
				// TODO: undo loading?
			}
		}
		quasicolon.getJDA().updateCommands().addCommands(applicationCommands).queue(); // TODO: only update if there are new/updated commands
	}
}
