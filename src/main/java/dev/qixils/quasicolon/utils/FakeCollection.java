/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.utils;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * An empty collection that pretends to be mutable (in that it does not throw exceptions).
 */
public class FakeCollection<E> implements Collection<E> {

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean contains(@Nullable Object o) {
		return false;
	}

	@NonNull
	@Override
	public Iterator<E> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Object @NonNull [] toArray() {
		return new Object[0];
	}

	@Override
	public <T> T @NonNull [] toArray(T @NonNull [] a) {
		return a;
	}

	@Override
	public boolean add(@Nullable E e) {
		return false;
	}

	@Override
	public boolean remove(@Nullable Object o) {
		return false;
	}

	@Override
	public boolean containsAll(@NonNull Collection<?> c) {
		return c.isEmpty();
	}

	@Override
	public boolean addAll(@Nullable Collection<? extends E> c) {
		return false;
	}

	@Override
	public boolean removeAll(@Nullable Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(@Nullable Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
	}
}
