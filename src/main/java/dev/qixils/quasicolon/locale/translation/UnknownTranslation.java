/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
