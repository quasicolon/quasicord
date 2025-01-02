/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.registry.impl

import dev.qixils.quasicord.registry.MappedRegistry

abstract class MappedRegistryImpl<T> protected constructor(id: String) : AbstractRegistryImpl<Map.Entry<String, T>>(id), MappedRegistry<T> {
    private val map: MutableMap<String, T> = mutableMapOf()

    @Throws(IllegalArgumentException::class)
    override fun register(key: String, value: T): T {
        require(!map.containsKey(key)) { "Key already registered: $key" }
        map.put(key, value)
        return value
    }

    override fun get(key: String): T? {
        return map[key]
    }

    override fun iterator(): Iterator<Map.Entry<String, T>> {
        return map.entries.iterator()
    }
}
