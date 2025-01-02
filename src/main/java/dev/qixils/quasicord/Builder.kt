/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord

/**
 * An object which builds a certain immutable object.
 */
interface Builder<R> {
    /**
     * Builds the object.
     *
     * @return the built object
     * @throws IllegalStateException a required field is missing
     */
    @Throws(IllegalStateException::class)
    fun build(): R
}
