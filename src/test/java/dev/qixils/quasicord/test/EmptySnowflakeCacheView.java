/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.test;

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
