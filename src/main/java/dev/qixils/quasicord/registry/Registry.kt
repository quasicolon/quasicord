/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.registry

import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * A generally static collection of unique objects which can be appended to and iterated over.
 */
interface Registry<T> : Iterable<T> {
    /**
     * The ID of the registry.
     *
     * @return the ID of the registry
     */
	val id: String

    /**
     * Appends an object to the registry.
     *
     * @param item the object to be appended
     * @throws IllegalStateException if the registry is [closed][.isClosed]
     * @return the value that was registered
     */
    @Throws(IllegalStateException::class)
    fun register(item: T): T

    val isClosed: Boolean
        /**
         * Checks whether the registry is closed.
         *
         * @return true if the registry is closed
         */
        get() = false

    /**
     * Streams this registry's values.
     *
     * @return a stream of this registry's values
     */
    fun stream(): Stream<T> {
        return StreamSupport.stream(spliterator(), false)
    }
}
