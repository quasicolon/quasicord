package dev.qixils.quasicolon.cogs;

import cloud.commandframework.Command;
import cloud.commandframework.jda.JDACommandSender;
import dev.qixils.quasicolon.Quasicolon;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;

/**
 * A module which adds functionality to {@link Quasicolon Quasicolon}.
 */
public interface Cog {

	/**
	 * Gets the instance of the {@link Quasicolon} library.
	 *
	 * @return library instance
	 */
	@NonNull
	Quasicolon getLibrary();

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
	@NonNull
	Collection<CommandData> getApplicationCommands();

	/**
	 * Gets the unmodifiable view of the custom commands this cog provides.
	 *
	 * @return provided custom commands
	 */
	@NonNull
	Collection<Command.Builder<JDACommandSender>> getCustomCommands();

}