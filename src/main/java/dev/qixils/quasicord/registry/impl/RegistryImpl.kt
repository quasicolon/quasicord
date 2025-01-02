/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.registry.impl

import java.util.*

open class RegistryImpl<T>(id: String) : AbstractRegistryImpl<T>(id) {
    private val values: MutableSet<T> = mutableSetOf()

    override fun register(item: T): T {
        check(!isClosed) { "Registry is closed" }
        values.add(item)
        return item
    }

    override fun iterator(): Iterator<T> {
        return values.iterator()
    }

    override fun spliterator(): Spliterator<T> {
        return values.spliterator()
    }
}
