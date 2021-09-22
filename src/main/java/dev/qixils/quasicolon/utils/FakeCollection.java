package dev.qixils.quasicolon.utils;

import org.jetbrains.annotations.NotNull;

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
	public boolean contains(Object o) {
		return false;
	}

	@NotNull
	@Override
	public Iterator<E> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Object @NotNull[] toArray() {
		return new Object[0];
	}

	@Override
	public <T> T @NotNull[] toArray(T @NotNull[] a) {
		return a;
	}

	@Override
	public boolean add(E e) {
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		return c.isEmpty();
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends E> c) {
		return false;
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {

	}
}
