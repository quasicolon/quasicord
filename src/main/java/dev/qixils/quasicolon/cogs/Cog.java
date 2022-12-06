/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs;

import dev.qixils.quasicolon.Quasicord;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;

/**
 * A module which adds functionality to {@link Quasicord Quasicord}.
 * <p>
 * Cogs are not automatically loaded. Please see the javadocs of the subclasses for more information
 * about how to load a cog.
 */
public interface Cog {

	/**
	 * Gets the instance of the {@link Quasicord} library.
	 *
	 * @return library instance
	 */
	@NonNull
	Quasicord getLibrary();

	/**
	 * Called when the cog is loaded.
	 */
	void onLoad();

	/**
	 * Gets the unmodifiable view of the application commands this cog provides.
	 *
	 * @return provided application commands
	 */
	@NonNull
	Collection<Command<?>> getCommands();
}
