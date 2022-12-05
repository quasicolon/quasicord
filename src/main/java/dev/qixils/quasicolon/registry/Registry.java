/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.registry;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A generally static collection of unique objects which can be appended to and iterated over.
 *
 * @param <T> the type of object to be stored in the registry
 */
public interface Registry<T> extends Iterable<T> {

	/**
	 * The ID of the registry.
	 *
	 * @return the ID of the registry
	 */
	@NonNull
	String getID();

	/**
	 * Appends an object to the registry.
	 *
	 * @param item the object to be appended
	 * @throws IllegalStateException if the registry is {@link #isClosed() closed}
	 * @return the value that was registered
	 */
	@NonNull
	T register(@NonNull T item) throws IllegalStateException;

	/**
	 * Checks whether the registry is closed.
	 *
	 * @return true if the registry is closed
	 */
	default boolean isClosed() {
		return false;
	}

	/**
	 * Streams this registry's values.
	 *
	 * @return a stream of this registry's values
	 */
	@NonNull
	default Stream<T> stream() {
		return StreamSupport.stream(spliterator(), false);
	}
}
