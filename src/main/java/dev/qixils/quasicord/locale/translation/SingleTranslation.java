/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.locale.translation;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A loaded translation with no plural forms.
 */
public interface SingleTranslation extends Translation {

	/**
	 * Gets the translated string.
	 *
	 * @return translated string
	 */
	@NonNull String get();
}
