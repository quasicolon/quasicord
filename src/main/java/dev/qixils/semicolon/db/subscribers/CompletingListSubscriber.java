package dev.qixils.semicolon.db.subscribers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

// boilerplate similar to java records
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class CompletingListSubscriber<T> implements Subscriber<T> {

	private final CompletableFuture<List<T>> future;
	private final List<T> elements = new ArrayList<>();

	@Override
	public void onSubscribe(Subscription subscription) {

	}

	@Override
	public void onNext(T t) {
		elements.add(t);
	}

	@Override
	public void onError(Throwable throwable) {
		future.completeExceptionally(throwable);
	}

	@Override
	public void onComplete() {
		future.complete(elements);
	}

}
