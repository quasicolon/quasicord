package dev.qixils.quasicolon.registry.impl;

import dev.qixils.quasicolon.registry.Registry;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractRegistryImpl<T> implements Registry<T> {
	private final @NonNull String id;

	protected AbstractRegistryImpl(@NonNull String id) {
		this.id = id;
	}

	@Override
	public @NonNull String getID() {
		return id;
	}
}
