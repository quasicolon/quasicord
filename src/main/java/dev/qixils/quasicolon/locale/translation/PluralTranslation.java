/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
