/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.locale.translation;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

/**
 * A translated object.
 */
public interface Translation {

	/**
	 * Gets the key of this translation.
	 *
	 * @return translation key
	 */
	@NonNull String getKey();

	/**
	 * Gets the locale of this translation.
	 *
	 * @return translation locale
	 */
	@NonNull Locale getLocale();

	/**
	 * Gets the originally requested locale for this translation.
	 * <p>
	 * This may be different from {@link #getLocale()} if the requested locale was unavailable or
	 * did not have a translation available for the requested key.
	 * </p>
	 *
	 * @return originally requested locale
	 */
	@NonNull Locale getRequestedLocale();
}
