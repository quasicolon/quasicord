/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.test;

import net.dv8tion.jda.api.utils.ClosableIterator;
import org.jetbrains.annotations.Contract;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class EmptyClosableIterator<T> implements ClosableIterator<T> {
    @SuppressWarnings("rawtypes")
    public static final ClosableIterator EMPTY_CLOSABLE_ITERATOR = new EmptyClosableIterator();

    @Override
    public void close() {}

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    @Contract("-> fail")
    public T next() throws NoSuchElementException {
        throw new NoSuchElementException("Iterator is empty");
    }

    @Override
    @Contract("-> fail")
    public void remove() throws NoSuchElementException {
        throw new IllegalStateException();
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {}
}
