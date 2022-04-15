/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.registry.impl;

import dev.qixils.quasicolon.registry.MappedRegistry;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public abstract class MappedRegistryImpl<T> extends AbstractRegistryImpl<Map.Entry<String, T>> implements MappedRegistry<T> {
	private final Map<String, T> map = new HashMap<>();

	protected MappedRegistryImpl(@NonNull String id) {
		super(id);
	}

	@NonNull
	public T register(@NonNull String key, @NonNull T value) throws IllegalArgumentException {
		if (map.containsKey(key))
			throw new IllegalArgumentException("Key already registered: " + key);
		map.put(key, value);
		return value;
	}

	public @NonNull Optional<T> get(@NonNull String key) {
		return Optional.ofNullable(map.get(key));
	}

	@NotNull
	@Override
	public Iterator<Entry<String, T>> iterator() {
		return map.entrySet().iterator();
	}
}
