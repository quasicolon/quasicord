/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.locale.translation.impl;

import dev.qixils.quasicolon.locale.translation.SingleTranslation;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

/**
 * Implementation of {@link SingleTranslation}.
 */
public final class SingleTranslationImpl extends AbstractTranslation implements SingleTranslation {
	private final @NonNull String translation;

	public SingleTranslationImpl(@NonNull String key,
								 @NonNull Locale locale,
								 @NonNull Locale requestedLocale,
								 @NonNull String translation) {
		super(key, locale, requestedLocale);
		this.translation = translation;
	}

	@Override
	public @NonNull String get() {
		return translation;
	}
}
