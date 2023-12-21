/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@UtilityClass
public class CollectionUtil {
	/**
	 * Returns the first element from the iterable that matches the filter.
	 *
	 * @param items collection of items
	 * @param filter filter that the returned item must match
	 * @return an item from the collection matching the predicate, or null if none is found
	 */
	@Nullable
	public <T> T first(@NotNull Iterable<T> items, @NotNull Predicate<T> filter) {
		Objects.requireNonNull(filter, "filter");

		for (T item : Objects.requireNonNull(items, "items")) {
			if (filter.test(item))
				return item;
		}

		return null;
	}

	/**
	 * Returns all the elements from a collection that match the filter.
	 *
	 * @param items collection of items
	 * @param filter filter that the returned items must match
	 * @return all items from the collection that match the predicate
	 */
	@NotNull
	public <T> List<T> all(@NotNull Iterable<T> items, @NotNull Predicate<T> filter) {
		Objects.requireNonNull(items, "items");
		Objects.requireNonNull(filter, "filter");

		List<T> results = new ArrayList<>();
		for (T item : items) {
			if (filter.test(item))
				results.add(item);
		}

		return results;
	}

	/**
	 * Returns all the elements from a collection that <b>don't</b> match the filter.
	 *
	 * @param items collection of items
	 * @param filter filter that the returned items must match
	 * @return all items from the collection that <b>don't</b> match the predicate
	 */
	@NotNull
	public <T> List<T> none(@NotNull Iterable<T> items, @NotNull Predicate<T> filter) {
		return all(items, Predicate.not(Objects.requireNonNull(filter, "filter")));
	}
}
