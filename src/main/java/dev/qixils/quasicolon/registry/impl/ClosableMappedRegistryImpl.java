package dev.qixils.quasicolon.registry.impl;

import dev.qixils.quasicolon.registry.ClosableRegistry;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;

public abstract class ClosableMappedRegistryImpl<T> extends MappedRegistryImpl<T> implements ClosableRegistry<Map.Entry<String, T>> {
	private boolean closed = false;
	private final boolean shouldClose;

	protected ClosableMappedRegistryImpl(@NonNull String id, boolean shouldClose) {
		super(id);
		this.shouldClose = shouldClose;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		if (closed)
			throw new IllegalStateException("Registry is already closed");
		closed = true;
	}

	@Override
	public boolean shouldClose() {
		return shouldClose;
	}
}
