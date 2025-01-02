/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.utils

import java.util.*
import java.util.function.Predicate

object CollectionUtil {
    /**
     * Returns the first element from the iterable that matches the filter.
     *
     * @param items collection of items
     * @param filter filter that the returned item must match
     * @return an item from the collection matching the predicate, or null if none is found
     */
    fun <T> first(items: Iterable<T>, filter: Predicate<T>): T? {
        Objects.requireNonNull(filter, "filter")

        for (item in Objects.requireNonNull(items, "items")) {
            if (filter.test(item)) return item
        }

        return null
    }

    /**
     * Returns all the elements from a collection that match the filter.
     *
     * @param items collection of items
     * @param filter filter that the returned items must match
     * @return all items from the collection that match the predicate
     */
    fun <T> all(items: Iterable<T>, filter: Predicate<T>): List<T> {
        Objects.requireNonNull(items, "items")
        Objects.requireNonNull(filter, "filter")

        val results: MutableList<T> = ArrayList()
        for (item in items) {
            if (filter.test(item)) results.add(item)
        }

        return results
    }

    /**
     * Returns all the elements from a collection that **don't** match the filter.
     *
     * @param items collection of items
     * @param filter filter that the returned items must match
     * @return all items from the collection that **don't** match the predicate
     */
    fun <T> none(items: Iterable<T>, filter: Predicate<T>): List<T> {
        return all(items, Predicate.not(Objects.requireNonNull(filter, "filter")))
    }
}
