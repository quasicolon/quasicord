/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.registry.impl;

import dev.qixils.quasicolon.registry.ClosableRegistry;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class ClosableRegistryImpl<T> extends RegistryImpl<T> implements ClosableRegistry<T> {
	private boolean closed = false;
	private final boolean shouldClose;

	protected ClosableRegistryImpl(@NonNull String id, boolean shouldClose) {
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
