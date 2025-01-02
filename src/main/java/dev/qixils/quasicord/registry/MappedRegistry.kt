/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.registry

import kotlin.Throws

/**
 * A registry that maps keys to values.
 *
 * @param <T> the type of values to be stored in the registry
</T> */
interface MappedRegistry<T> : Registry<Map.Entry<String, T>> {
    /**
     * Registers a value under the given key.
     *
     * @param key   the key to register the value under
     * @param value the value to register
     * @throws IllegalArgumentException if the key is already registered
     * @throws IllegalStateException    if the registry is closed
     * @return the value that was registered
     */
    @Throws(IllegalArgumentException::class)
    fun register(key: String, value: T): T

    /**
     * Registers a value under the given key.
     * You should prefer [.register] instead.
     *
     * @param item the object to be appended
     * @throws IllegalArgumentException if the key is already registered
     * @throws IllegalStateException    if the registry is closed
     * @return the value that was registered
     */
    @Deprecated(
        """use {@link #register(String, Object)} instead
	  """
    )
    @Throws(IllegalArgumentException::class)
    override fun register(item: Map.Entry<String, T>): Map.Entry<String, T> {
        register(item.key, item.value)
        return item
    }

    /**
     * Gets the value associated with the given key.
     *
     * @param key the key to get the value for
     * @return the value associated with the given key, or null if no value is
     * registered under the given key
     */
    fun get(key: String): T?
}
