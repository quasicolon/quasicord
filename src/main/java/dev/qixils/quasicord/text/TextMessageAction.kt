/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.text;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.internal.requests.restaction.MessageCreateActionImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TextMessageAction extends MessageCreateActionImpl {
	private final @NonNull Mono<String> text;

	public TextMessageAction(@NonNull MessageChannel channel, @NonNull Mono<String> text) {
		super(channel);
		this.text = text;
	}

	@Override
	public @NonNull MessageCreateAction addContent(@NonNull String content) {
		throw new UnsupportedOperationException("Cannot add content to a TextMessageAction");
	}

	@Override
	public @NonNull MessageCreateAction setContent(@Nullable String content) {
		throw new UnsupportedOperationException("Cannot set content of a TextMessageAction");
	}

	// TODO: create wrapper class for the internal builder to also prevent setting content there?
	//       would need to find a workaround for the super.setContent(...) call below

	@Override
	public void queue(@Nullable Consumer<? super Message> success, @Nullable Consumer<? super Throwable> failure) {
		text.subscribe(super::setContent, failure, () -> super.queue(success, failure));
	}

	@NotNull
	@Override
	public @NonNull CompletableFuture<Message> submit(boolean shouldQueue) {
		return text.toFuture().thenCompose(content -> {
			super.setContent(content);
			return super.submit(shouldQueue);
		});
	}

	@Override
	public @NonNull Message complete(boolean shouldQueue) throws RateLimitedException {
		super.setContent(text.block());
		return super.complete(shouldQueue);
	}
}
