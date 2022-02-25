package dev.qixils.quasicolon.test;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.jetbrains.annotations.Nullable;

public class EmptySnowflakeCacheView<T extends ISnowflake> extends EmptyCacheView<T> implements SnowflakeCacheView<T> {
    @Nullable
    @Override
    public T getElementById(long id) {
        return null;
    }

    @SuppressWarnings("rawtypes")
    private static final EmptySnowflakeCacheView EMPTY_SNOWFLAKE_CACHE_VIEW = new EmptySnowflakeCacheView();

    public static <T extends ISnowflake> SnowflakeCacheView<T> emptySnowflakeCacheView() {
        //noinspection unchecked
        return (SnowflakeCacheView<T>) EMPTY_SNOWFLAKE_CACHE_VIEW;
    }
}