package dev.qixils.quasicolon.db.subscribers;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.CompletableFuture;

public record CompletingSubscriber<T>(CompletableFuture<T> future) implements Subscriber<T> {

	@Override
	public void onSubscribe(Subscription subscription) {

	}

	@Override
	public void onNext(T t) {
		future.complete(t);
	}

	@Override
	public void onError(Throwable throwable) {
		future.completeExceptionally(throwable);
	}

	@Override
	public void onComplete() {
		future.complete(null); // will do nothing if already completed
	}
}
