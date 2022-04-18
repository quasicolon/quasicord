/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs;

import cloud.commandframework.Command;
import cloud.commandframework.jda.JDACommandSender;
import dev.qixils.quasicolon.Quasicolon;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;

/**
 * A module which adds functionality to {@link Quasicolon Quasicolon}.
 * <p>
 * Cogs are not automatically loaded. Please see the javadocs of subclasses for more information
 * about how to load a cog.
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
	Collection<ApplicationCommand<?>> getApplicationCommands();

	/**
	 * Gets the unmodifiable view of the custom commands this cog provides.
	 *
	 * @return provided custom commands
	 */
	@NonNull
	Collection<Command<JDACommandSender>> getCustomCommands();

}
