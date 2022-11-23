/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.registry.core;

import dev.qixils.quasicolon.Quasicord;
import dev.qixils.quasicolon.registry.ClosableRegistry;
import dev.qixils.quasicolon.registry.Registry;
import dev.qixils.quasicolon.registry.impl.ClosableMappedRegistryImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * The registry of registries.
 */
public final class RegistryRegistry extends ClosableMappedRegistryImpl<Registry<?>> {

	private final @NonNull Quasicord quasicord;

	/**
	 * The registry of {@link dev.qixils.quasicolon.variables.parsers.VariableParser variables}.
	 */
	public final VariableRegistry VARIABLE_REGISTRY;

	/**
	 * The registry of {@link dev.qixils.quasicolon.cogs.GlobalCog global cogs}.
	 */
	public final GlobalCogRegistry GLOBAL_COG_REGISTRY;

	/**
	 * Creates a new root registry.
	 *
	 * @param quasicord the quasicord instance
	 */
	@Internal
	public RegistryRegistry(@NonNull Quasicord quasicord) {
		super("registries", false);
		this.quasicord = quasicord;
		register(this);
		VARIABLE_REGISTRY = register(new VariableRegistry(quasicord));
		GLOBAL_COG_REGISTRY = register(new GlobalCogRegistry(quasicord));
		close();
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
		if (value instanceof ClosableRegistry<?> closable && closable.shouldClose() && !closable.isClosed())
			closable.close();
		return value;
	}
}
