package dev.qixils.quasicolon.locale.text;

import dev.qixils.quasicolon.locale.TranslationProvider;
import dev.qixils.quasicolon.locale.TranslationProvider.Type;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Localizable text that has no plural forms.
 */
public class SingleLocalizableText extends AbstractLocalizableText {
	public SingleLocalizableText(@NonNull TranslationProvider translationProvider, @NonNull String key, String @Nullable [] args) {
		super(translationProvider, key, args);
	}

	public SingleLocalizableText(@NonNull Type translationProviderType, @NonNull String key, String @Nullable [] args) {
		super(translationProviderType, key, args);
	}

	@Override
	public @NonNull String asString(@NonNull Locale locale) {
		return new MessageFormat(translationProvider.getSingle(key, locale).get(), locale).format(args);
	}
}
