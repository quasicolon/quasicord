package dev.qixils.quasicolon.cogs.impl;

import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.cogs.GuildCog;
import org.checkerframework.checker.nullness.qual.NonNull;

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
		getCustomCommands().forEach(command -> library.getCommandManager().command(command.meta(Quasicolon.GUILD_KEY, guildId)));
	}
}
