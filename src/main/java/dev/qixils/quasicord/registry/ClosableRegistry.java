/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.registry;

import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * A registry whose registration can be closed, meaning it will no longer accept new entries.
 */
public interface ClosableRegistry<T> extends Registry<T> {

	/**
	 * Closes the registry, meaning it will no longer accept new entries.
	 * This is for internal use only.
	 *
	 * @throws IllegalStateException if the registry is already closed
	 */
	@Internal
	void close() throws IllegalStateException;

	/**
	 * Checks whether the registry is closed.
	 *
	 * @return true if the registry is closed
	 */
	@Override
	boolean isClosed();

	/**
	 * Checks whether the registry should be immediately closed after initialization.
	 *
	 * @return true if the registry should be immediately closed
	 */
	boolean shouldClose();
}
