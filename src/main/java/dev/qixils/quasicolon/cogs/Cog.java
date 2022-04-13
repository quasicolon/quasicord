package dev.qixils.quasicolon.cogs;

import cloud.commandframework.jda.JDACommandSender;
import dev.qixils.quasicolon.Quasicolon;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A module which adds functionality to {@link Quasicolon Quasicolon}.
 */
public interface Cog {

	/**
	 * Called when the cog is loaded.
	 */
	void onLoad();

	/**
	 * Gets the unmodifiable view of the application commands this cog provides.
	 * These are official Discord commands (i.e. slash commands), not to be confused with
	 * custom commands.
	 *
	 * @return provided application commands
	 */
	@NotNull
	Collection<CommandData> getApplicationCommands();

	/**
	 * Gets the unmodifiable view of the custom commands this cog provides.
	 *
	 * @return provided custom commands
	 */
	@NotNull
	Collection<cloud.commandframework.Command<JDACommandSender>> customCommands();

}
