package dev.qixils.quasicolon.locale.translation;

import net.xyzsd.plurals.PluralRuleType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A translation that is not known to the library. This implements the methods of the two main
 * translation interfaces but will return the translation key instead of a translation.
 */
public interface UnknownTranslation extends SingleTranslation, PluralTranslation {

	@Override
	default @NonNull String get() {
		return getKey();
	}

	@Override
	default @NonNull String get(int quantity, @NonNull PluralRuleType ruleType) {
		return getKey();
	}
}
