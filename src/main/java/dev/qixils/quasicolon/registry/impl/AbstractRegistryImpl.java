/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
