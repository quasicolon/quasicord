/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.registry.core;

import dev.qixils.quasicord.Quasicord;
import dev.qixils.quasicord.converter.ConverterRegistry;
import dev.qixils.quasicord.registry.Registry;
import dev.qixils.quasicord.registry.impl.MappedRegistryImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * The registry of registries.
 */
public final class RegistryRegistry extends MappedRegistryImpl<Registry<?>> {

	private final @NonNull Quasicord quasicord;

	/**
	 * The registry of {@link dev.qixils.quasicord.variables.parsers.VariableParser variables}.
	 */
	public final VariableRegistry VARIABLE_REGISTRY;

	/**
	 * The registry of {@link dev.qixils.quasicord.converter.Converter converters}.
	 */
	public final ConverterRegistry CONVERTER_REGISTRY;

	/**
	 * Creates a new root registry.
	 *
	 * @param quasicord the quasicord instance
	 */
	@Internal
	public RegistryRegistry(@NonNull Quasicord quasicord) {
		super("registries");
		this.quasicord = quasicord;
		register(this);
		VARIABLE_REGISTRY = register(new VariableRegistry());
		CONVERTER_REGISTRY = register(new ConverterRegistry(quasicord));
	}

	/**
	 * Registers a registry.
	 *
	 * @param registry the registry to register
	 * @return the registry
	 */
	public <T extends Registry<?>> T register(@NonNull T registry) {
		register(registry.getID(), registry);
		return registry;
	}

	/**
	 * Registers a registry with the given ID.
	 *
	 * @param key   the key to register the value under
	 * @param value the value to register
	 * @throws IllegalArgumentException if the key is already registered
	 * @throws IllegalStateException    if the registry is closed
	 * @deprecated use {@link #register(Registry)} instead
	 */
	@Override
	@Deprecated
	@NonNull
	public Registry<?> register(@NonNull String key, @NonNull Registry<?> value) throws IllegalArgumentException {
		super.register(key, value);
		quasicord.getEventDispatcher().dispatchRegistryInit(value);
		return value;
	}
}
