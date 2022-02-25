package dev.qixils.quasicolon.error;

import dev.qixils.quasicolon.locale.TranslationProvider;
import dev.qixils.quasicolon.text.LocalizableText;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

// lame copy+pasted code from LocalizableText because java inheritance is dumb
public class LocalizedException extends Exception implements LocalizableText {
	protected final @NonNull TranslationProvider translationProvider;
	private final @Getter @NonNull String key;
	private final @Getter String @NonNull [] args;

	public LocalizedException(@NonNull TranslationProvider translationProvider, @NonNull String key, String @Nullable ... args) {
		this.translationProvider = translationProvider;
		this.key = Objects.requireNonNull(key, "key");
		this.args = Objects.requireNonNullElseGet(args, () -> new String[0]);
	}

	public LocalizedException(TranslationProvider.@NonNull Type translationProviderType, @NonNull String key, String @Nullable ... args) {
		this(translationProviderType.getInstance(), key, args);
	}

	@Override
	public @NonNull String asString(@NonNull Locale locale) {
		return new MessageFormat(translationProvider.getSingle(key, locale).get(), locale).format(args);
	}
}
