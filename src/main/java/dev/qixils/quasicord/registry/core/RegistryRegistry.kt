/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.registry.core

import dev.qixils.quasicord.Quasicord
import dev.qixils.quasicord.converter.ConverterRegistry
import dev.qixils.quasicord.registry.Registry
import dev.qixils.quasicord.registry.impl.MappedRegistryImpl
import org.jetbrains.annotations.ApiStatus
import kotlin.Throws

/**
 * The registry of registries.
 */
class RegistryRegistry @ApiStatus.Internal constructor(private val quasicord: Quasicord) : MappedRegistryImpl<Registry<*>>("registries") {

    /**
     * The registry of [converters][dev.qixils.quasicord.converter.Converter].
     */
    val converterRegistry: ConverterRegistry

    /**
     * Creates a new root registry.
     *
     * @param quasicord the quasicord instance
     */
    init {
        register(this)
        converterRegistry = register(ConverterRegistry(quasicord))
    }

    /**
     * Registers a registry.
     *
     * @param registry the registry to register
     * @return the registry
     */
	@Suppress("DEPRECATION")
	fun <T : Registry<*>> register(registry: T): T {
        register(registry.id, registry)
        return registry
    }

    /**
     * Registers a registry with the given ID.
     *
     * @param key   the key to register the value under
     * @param value the value to register
     * @throws IllegalArgumentException if the key is already registered
     * @throws IllegalStateException    if the registry is closed
     */
    @Deprecated("use {@link #register(Registry)} instead")
    @Throws(IllegalArgumentException::class)
    override fun register(key: String, value: Registry<*>): Registry<*> {
        super.register(key, value)
        quasicord.eventDispatcher.dispatchRegistryInit(value)
        return value
    }
}
