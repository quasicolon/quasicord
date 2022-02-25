package dev.qixils.quasicolon.locale.text;

import dev.qixils.quasicolon.locale.TranslationProvider;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

public abstract class AbstractLocalizableText implements LocalizableText {
	protected final @NonNull TranslationProvider translationProvider;
	protected final @Getter @NonNull String key;
	protected final @Getter String @NonNull [] args;

	protected AbstractLocalizableText(@NonNull TranslationProvider translationProvider, @NonNull String key, String @Nullable [] args) {
		this.translationProvider = translationProvider;
		this.key = key;
		this.args = Objects.requireNonNullElseGet(args, () -> new String[0]);
	}

	protected AbstractLocalizableText(TranslationProvider.@NonNull Type translationProviderType, @NonNull String key, String @Nullable [] args) {
		this(translationProviderType.getInstance(), key, args);
	}
}
