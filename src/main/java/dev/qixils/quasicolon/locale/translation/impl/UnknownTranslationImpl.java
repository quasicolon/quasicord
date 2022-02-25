package dev.qixils.quasicolon.locale.translation.impl;

import dev.qixils.quasicolon.locale.translation.UnknownTranslation;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

/**
 * An implementation of {@link UnknownTranslation}.
 */
public class UnknownTranslationImpl extends AbstractTranslation implements UnknownTranslation {

	/**
	 * Initializes a new unknown translation.
	 *
	 * @param key    the key of the translation
	 * @param locale the locale of the translation
	 */
	public UnknownTranslationImpl(@NonNull String key,
								  @NonNull Locale locale,
								  @NonNull Locale requestedLocale) {
		super(key, locale, requestedLocale);
	}
}
