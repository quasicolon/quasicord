/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.registry.impl;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;

public abstract class RegistryImpl<T> extends AbstractRegistryImpl<T> {
	private final Set<T> values = new HashSet<>();

	protected RegistryImpl(@NonNull String id) {
		super(id);
	}

	@Override
	@NonNull
	public T register(@NonNull T item) {
		if (isClosed())
			throw new IllegalStateException("Registry is closed");
		values.add(item);
		return item;
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return values.iterator();
	}

	@Override
	public Spliterator<T> spliterator() {
		return values.spliterator();
	}
}
