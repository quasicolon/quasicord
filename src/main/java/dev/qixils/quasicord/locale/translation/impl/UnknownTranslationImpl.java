/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.locale.translation.impl;

import dev.qixils.quasicord.locale.translation.UnknownTranslation;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

/**
 * An implementation of {@link UnknownTranslation}.
 */
public class UnknownTranslationImpl extends AbstractTranslation implements UnknownTranslation {

	/**
	 * Initializes a new unknown translation.
	 *
	 * @param key             the key of the translation
	 * @param requestedLocale the requested locale
	 */
	public UnknownTranslationImpl(@NonNull String key,
								  @NonNull Locale requestedLocale) {
		super(key, Locale.ROOT, requestedLocale);
	}
}
