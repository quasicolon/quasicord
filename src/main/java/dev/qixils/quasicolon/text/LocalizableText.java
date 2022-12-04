/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.text;

import dev.qixils.quasicolon.locale.Localizable;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Text that can be localized into various languages.
 */
public interface LocalizableText extends Localizable, Text {

	/**
	 * Returns the formatting arguments that will be used to format the text.
	 *
	 * @return the formatting arguments
	 */
	Object @NonNull [] getArgs();
}
