/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.test.actions;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class DummyRestAction<T> implements CacheRestAction<T> {
	protected final JDA jda;
	protected final T value;

	public DummyRestAction(JDA jda, @Nullable T value) {
		this.jda = jda;
		this.value = value;
	}

	public DummyRestAction(JDA jda) {
		this(jda, null);
	}

	@NotNull
	@Override
	public JDA getJDA() {
		return jda;
	}

	@NotNull
	@Override
	public CacheRestAction<T> setCheck(@Nullable BooleanSupplier checks) {
		return this;
	}

	@NotNull
	@Override
	public CacheRestAction<T> useCache(boolean useCache) {
		return this;
	}

	@Override
	public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
		if (success != null) success.accept(value);
	}

	@Override
	public T complete(boolean shouldQueue) {
		return value;
	}

	@NotNull
	@Override
	public CompletableFuture<T> submit(boolean shouldQueue) {
		return CompletableFuture.completedFuture(value);
	}
}
