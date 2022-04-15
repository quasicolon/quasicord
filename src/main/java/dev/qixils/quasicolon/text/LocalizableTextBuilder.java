package dev.qixils.quasicolon.text;

import dev.qixils.quasicolon.Builder;
import dev.qixils.quasicolon.Key;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

/**
 * A generic builder for creating {@link LocalizableText} instances.
 *
 * @param <B> the type of the builder
 * @param <R> the type of the localizable text to build
 */
@SuppressWarnings("unchecked")
abstract class LocalizableTextBuilder<B extends LocalizableTextBuilder<B, R>, R extends LocalizableText> implements Builder<R> {
	protected @Nullable Key key;
	protected Object @NonNull [] args = new Object[]{};

	LocalizableTextBuilder() {
	}

	/**
	 * Sets the translation key.
	 *
	 * @param key translation key
	 * @return this builder
	 */
	@Contract("_ -> this")
	public @NonNull B key(@NonNull Key key) {
		this.key = key;
		return (B) this;
	}

	/**
	 * Sets the arguments used to format the translated string.
	 *
	 * @param args arguments
	 * @return this builder
	 */
	@Contract("_ -> this")
	public @NonNull B args(Object @NonNull ... args) {
		this.args = args;
		return (B) this;
	}
}
