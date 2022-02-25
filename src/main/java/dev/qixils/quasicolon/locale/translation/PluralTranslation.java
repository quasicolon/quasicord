package dev.qixils.quasicolon.locale.translation;

import net.xyzsd.plurals.PluralRuleType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A translation which has multiple string literals
 * corresponding to the quantity of a specific argument.
 */
public interface PluralTranslation extends Translation {

	/**
	 * Gets the translation corresponding to the given quantity.
	 *
	 * @param quantity the quantity to get the translation for
	 * @param ruleType the rule type (ordinal or cardinal) to determine the plural form
	 * @return the corresponding translation
	 */
	@NonNull String get(int quantity, @NonNull PluralRuleType ruleType);
}
