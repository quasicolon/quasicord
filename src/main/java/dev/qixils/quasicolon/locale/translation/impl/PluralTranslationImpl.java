package dev.qixils.quasicolon.locale.translation.impl;

import dev.qixils.quasicolon.locale.translation.PluralTranslation;
import net.xyzsd.plurals.PluralCategory;
import net.xyzsd.plurals.PluralRule;
import net.xyzsd.plurals.PluralRuleType;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation of {@link PluralTranslation}.
 */
public final class PluralTranslationImpl extends AbstractTranslation implements PluralTranslation {
	private final @NonNull Map<PluralCategory, String> translations;

	/**
	 * Initializes a new plural translation.
	 *
	 * @param key          the key of the translation
	 * @param locale       the locale of the translation
	 * @param translations the translations for each {@link PluralCategory}
	 */
	public PluralTranslationImpl(@NonNull String key,
								 @NonNull Locale locale,
								 @NonNull Locale requestedLocale,
								 @NonNull Map<PluralCategory, String> translations) {
		super(key, locale, requestedLocale);
		this.translations = translations;
	}

	@Override
	public @NonNull String get(int quantity, @NonNull PluralRuleType ruleType) {
		return translations.get(PluralRule.createOrDefault(getLocale(), ruleType).select(quantity));
	}

	public static PluralTranslationImpl fromStringMap(@NonNull String key,
													  @NonNull Locale locale,
													  @NonNull Locale requestedLocale,
													  @NonNull Map<String, String> translations) {
		Map<PluralCategory, String> pluralTranslations = new EnumMap<>(PluralCategory.class);
		for (Map.Entry<String, String> entry : translations.entrySet()) {
			pluralTranslations.put(PluralCategory.valueOf(entry.getKey().toUpperCase(Locale.ROOT)), entry.getValue());
		}
		return new PluralTranslationImpl(key, locale, requestedLocale, pluralTranslations);
	}
}
