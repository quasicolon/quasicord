/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.text;

import dev.qixils.quasicolon.Key;
import lombok.Getter;
import net.xyzsd.plurals.PluralRuleType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Localizable text that has plural forms.
 */
@Getter
public class PluralLocalizableText extends AbstractLocalizableText {
	private final int quantity;
	private final @NonNull PluralRuleType ruleType;

	PluralLocalizableText(int quantity, @NonNull PluralRuleType ruleType, @NonNull Key key, Object @Nullable ... args) {
		super(key, args);
		this.quantity = quantity;
		this.ruleType = ruleType;
	}

	@Override
	public @NonNull String asString(@NonNull Locale locale) {
		return new MessageFormat(key.getPlural(locale).get(quantity, ruleType), locale).format(Text.localizeArgs(args, locale));
	}

	/**
	 * Builder for {@link PluralLocalizableText}.
	 * @see Text#plural() Text.plural() to create a new builder.
	 */
	public static final class Builder extends LocalizableTextBuilder<Builder, PluralLocalizableText> {
		private @Nullable Integer quantity;
		private @Nullable PluralRuleType ruleType;

		Builder() {
		}

		/**
		 * Sets the quantity used to determine the plural form.
		 *
		 * @param quantity integer quantity
		 * @return this builder
		 */
		public @NonNull Builder quantity(int quantity) {
			this.quantity = quantity;
			return this;
		}

		/**
		 * Sets the rule type used to determine the plural form.
		 *
		 * @param ruleType rule type
		 * @return this builder
		 */
		public @NonNull Builder ruleType(@NonNull PluralRuleType ruleType) {
			this.ruleType = ruleType;
			return this;
		}

		@Override
		public @NonNull PluralLocalizableText build() throws IllegalStateException {
			if (key == null)
				throw new IllegalStateException("Translation key is not set");
			if (quantity == null)
				throw new IllegalStateException("Quantity is not set");
			if (ruleType == null)
				throw new IllegalStateException("Rule type is not set");
			return new PluralLocalizableText(quantity, ruleType, key, args);
		}
	}
}
