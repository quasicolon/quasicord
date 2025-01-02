/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.text;


import dev.qixils.quasicord.Key;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A class that simply forwards all calls to another {@link LocalizableText} instance.
 */
public interface ForwardingLocalizedText extends ForwardingText, LocalizableText {

	/**
	 * Returns the {@link LocalizableText} instance that this class forwards all calls to.
	 *
	 * @return delegated {@link LocalizableText} instance
	 */
	@Override
	@NonNull LocalizableText getText();

	@Override
	default @NonNull Key getKey() {
		return getText().getKey();
	}

	@Override
	default Object @NonNull [] getArgs() {
		return getText().getArgs();
	}
}
