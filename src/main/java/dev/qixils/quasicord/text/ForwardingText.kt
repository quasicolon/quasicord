/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.text;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

/**
 * A class that simply forwards all calls to another {@link Text} instance.
 */
public interface ForwardingText extends Text {

	/**
	 * Returns the {@link Text} instance that this class forwards all calls to.
	 *
	 * @return delegated {@link Text} instance
	 */
	@SuppressWarnings("EmptyMethod") // this warning is just wrong
	@NonNull Text getText();

	@Override
	default @NonNull String asString(@NonNull Locale locale) {
		return getText().asString(locale);
	}
}
