/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.locale.translation.impl;

import dev.qixils.quasicord.locale.translation.Translation;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

/**
 * Abstract implementation of {@link Translation}.
 */
public abstract class AbstractTranslation implements Translation {
	private final @NonNull String key;
	private final @NonNull Locale locale;
	private final @NonNull Locale requestedLocale;

	/**
	 * Initializes common translation fields.
	 *
	 * @param key    the key of the translation
	 * @param locale the locale of the translation
	 */
	protected AbstractTranslation(@NonNull String key, @NonNull Locale locale, @NonNull Locale requestedLocale) {
		this.key = key;
		this.locale = locale;
		this.requestedLocale = requestedLocale;
	}

	@Override
	public @NonNull String getKey() {
		return key;
	}

	@Override
	public @NonNull Locale getLocale() {
		return locale;
	}

	@Override
	public @NonNull Locale getRequestedLocale() {
		return requestedLocale;
	}
}
