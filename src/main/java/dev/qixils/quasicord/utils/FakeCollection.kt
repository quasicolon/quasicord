/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.utils

import java.util.*

/**
 * An empty collection that pretends to be mutable (in that it does not throw exceptions).
 */
class FakeCollection<E> : MutableCollection<E?> {

	override val size: Int = 0

    override fun isEmpty(): Boolean {
        return true
    }

	override fun contains(element: E?): Boolean {
		return false
	}

    override fun iterator(): MutableIterator<E?> {
        return Collections.emptyIterator<E?>()
    }

    override fun add(e: E?): Boolean {
        return false
    }

    override fun remove(element: E?): Boolean {
        return false
    }

	override fun containsAll(elements: Collection<E?>): Boolean {
		return elements.isEmpty()
	}

    override fun addAll(elements: Collection<E?>): Boolean {
        return false
    }

    override fun removeAll(elements: Collection<E?>): Boolean {
        return false
    }

    override fun retainAll(elements: Collection<E?>): Boolean {
        return false
    }

    override fun clear() {
    }
}
