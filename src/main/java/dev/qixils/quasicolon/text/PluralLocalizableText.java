package dev.qixils.quasicolon.text;

import dev.qixils.quasicolon.locale.TranslationProvider;
import dev.qixils.quasicolon.locale.TranslationProvider.Type;
import lombok.Getter;
import net.xyzsd.plurals.PluralRuleType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Localizable text that has plural forms.
 */
@Getter
public class PluralLocalizableText extends AbstractLocalizableText {
	private final int quantity;
	private final @NonNull PluralRuleType ruleType;

	public PluralLocalizableText(@NonNull TranslationProvider translationProvider,
								 int quantity,
								 @NonNull PluralRuleType ruleType,
								 @NonNull String key,
								 String @Nullable ... args) {
		super(translationProvider, key, args);
		this.quantity = quantity;
		this.ruleType = ruleType;
	}

	public PluralLocalizableText(@NonNull Type translationProviderType,
								 int quantity,
								 @NonNull PluralRuleType ruleType,
								 @NonNull String key,
								 String @Nullable ... args) {
		super(translationProviderType, key, args);
		this.quantity = quantity;
		this.ruleType = ruleType;
	}

	@Override
	public @NonNull String asString(@NonNull Locale locale) {
		return new MessageFormat(translationProvider.getPlural(key, locale).get(quantity, ruleType), locale).format(args);
	}
}