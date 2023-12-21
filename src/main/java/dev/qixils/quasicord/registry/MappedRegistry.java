/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.registry;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map.Entry;
import java.util.Optional;

/**
 * A registry that maps keys to values.
 * 
 * @param <T> the type of values to be stored in the registry
 */
public interface MappedRegistry<T> extends Registry<Entry<String, T>> {

	/**
	 * Registers a value under the given key.
	 *
	 * @param key   the key to register the value under
	 * @param value the value to register
	 * @throws IllegalArgumentException if the key is already registered
	 * @throws IllegalStateException    if the registry is closed
	 * @return the value that was registered
	 */
	@NonNull
	T register(@NonNull String key, @NonNull T value) throws IllegalArgumentException;

	/**
	 * Registers a value under the given key.
	 * You should prefer {@link #register(String, Object)} instead.
	 *
	 * @param item the object to be appended
	 * @throws IllegalArgumentException if the key is already registered
	 * @throws IllegalStateException    if the registry is closed
	 * @deprecated use {@link #register(String, Object)} instead
	 * @return the value that was registered
	 */
	@Override
	@Deprecated
	@NonNull
	default Entry<String, T> register(@NonNull Entry<String, T> item) throws IllegalArgumentException {
		register(item.getKey(), item.getValue());
		return item;
	}

	/**
	 * Gets the value associated with the given key.
	 *
	 * @param key the key to get the value for
	 * @return the value associated with the given key, or {@link Optional#empty()} if no value is
	 *         registered under the given key
	 */
	@NonNull
	Optional<T> get(@NonNull String key);
}
