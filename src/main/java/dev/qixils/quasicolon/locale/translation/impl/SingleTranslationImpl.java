package dev.qixils.quasicolon.locale.translation.impl;

import dev.qixils.quasicolon.locale.translation.SingleTranslation;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

/**
 * Implementation of {@link SingleTranslation}.
 */
public final class SingleTranslationImpl extends AbstractTranslation implements SingleTranslation {
	private final @NonNull String translation;

	public SingleTranslationImpl(@NonNull String key,
								 @NonNull Locale locale,
								 @NonNull Locale requestedLocale,
								 @NonNull String translation) {
		super(key, locale, requestedLocale);
		this.translation = translation;
	}

	@Override
	public @NonNull String get() {
		return translation;
	}
}
