/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.text;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TextMessageAction extends MessageActionImpl {
	private final @NonNull CompletableFuture<Void> future = new CompletableFuture<>();

	public TextMessageAction(@NonNull JDA api, @Nullable String messageId, @NonNull MessageChannel channel, @NonNull Mono<String> text) {
		super(api, messageId, channel);
		text.subscribe(content::append, future::completeExceptionally, () -> future.complete(null));
	}

	@NotNull
	@Override
	public @NonNull MessageAction append(@Nullable CharSequence csq) {
		throw new UnsupportedOperationException("Cannot set content of a TextMessageAction");
	}

	@NotNull
	@Override
	public @NonNull MessageActionImpl append(@Nullable CharSequence csq, int start, int end) {
		throw new UnsupportedOperationException("Cannot set content of a TextMessageAction");
	}

	@NotNull
	@Override
	public @NonNull MessageActionImpl append(char c) {
		throw new UnsupportedOperationException("Cannot set content of a TextMessageAction");
	}

	@NotNull
	@Override
	public @NonNull MessageAction appendFormat(@Nullable String format, Object... args) {
		throw new UnsupportedOperationException("Cannot set content of a TextMessageAction");
	}

	@NotNull
	@Override
	public @NonNull MessageActionImpl content(@Nullable String content) {
		throw new UnsupportedOperationException("Cannot set content of a TextMessageAction");
	}

	@Override
	public void queue(@Nullable Consumer<? super Message> success, @Nullable Consumer<? super Throwable> failure) {
		future.handle((v, throwable) -> {
			if (throwable != null) {
				if (failure != null)
					failure.accept(throwable);
			} else
				super.queue(success, failure);
			return null;
		});
	}

	@NotNull
	@Override
	public @NonNull CompletableFuture<Message> submit(boolean shouldQueue) {
		return future.thenCompose(v -> super.submit(shouldQueue));
	}

	@Override
	public @NonNull Message complete(boolean shouldQueue) throws RateLimitedException {
		future.join();
		return super.complete(shouldQueue);
	}
}
