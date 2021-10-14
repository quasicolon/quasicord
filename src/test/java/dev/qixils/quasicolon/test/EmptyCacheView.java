package dev.qixils.quasicolon.test;

import net.dv8tion.jda.api.utils.ClosableIterator;
import net.dv8tion.jda.api.utils.cache.CacheView;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class EmptyCacheView<T> implements CacheView<T> {
    @SuppressWarnings("unchecked")
    private final ClosableIterator<T> iterator = (ClosableIterator<T>) EmptyClosableIterator.EMPTY_CLOSABLE_ITERATOR;

    @NotNull
    @Override
    public List<T> asList() {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public Set<T> asSet() {
        return Collections.emptySet();
    }

    @NotNull
    @Override
    public ClosableIterator<T> lockedIterator() {
        return iterator;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @NotNull
    @Override
    public List<T> getElementsByName(@NotNull String name, boolean ignoreCase) {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }

    @NotNull
    @Override
    public Stream<T> parallelStream() {
        return stream().parallel();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return iterator;
    }

    @SuppressWarnings("rawtypes")
    private static final EmptyCacheView EMPTY_CACHE_VIEW = new EmptyCacheView();

    public static <T> CacheView<T> emptyCacheView() {
        //noinspection unchecked
        return (CacheView<T>) EMPTY_CACHE_VIEW;
    }
}
