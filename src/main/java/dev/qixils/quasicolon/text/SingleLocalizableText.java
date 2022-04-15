/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.text;

import dev.qixils.quasicolon.Key;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Localizable text that has no plural forms.
 */
public final class SingleLocalizableText extends AbstractLocalizableText {
	SingleLocalizableText(@NonNull Key key, Object @Nullable [] args) {
		super(key, args);
	}

	@Override
	public @NonNull String asString(@NonNull Locale locale) {
		return new MessageFormat(key.getSingle(locale).get(), locale).format(Text.localizeArgs(args, locale));
	}

	/**
	 * Builder for {@link SingleLocalizableText}.
	 */
	public static final class Builder extends LocalizableTextBuilder<Builder, SingleLocalizableText> {
		Builder() {
		}

		@Override
		public @NonNull SingleLocalizableText build() throws IllegalStateException {
			if (key == null)
				throw new IllegalStateException("Translation key is not set");
			return new SingleLocalizableText(key, args);
		}
	}
}
