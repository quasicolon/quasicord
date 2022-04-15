/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.locale;

import dev.qixils.quasicolon.Key;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An object which possesses a translation key.
 */
public interface Localizable {

	/**
	 * Translation key corresponding to this object.
	 *
	 * @return translation key
	 */
	@NonNull Key getKey();
}
