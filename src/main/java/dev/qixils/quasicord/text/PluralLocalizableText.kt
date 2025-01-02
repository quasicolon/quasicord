/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.text

import dev.qixils.quasicord.Key
import net.xyzsd.plurals.PluralRuleType
import java.text.MessageFormat
import java.util.*
import kotlin.Throws

/**
 * Localizable text that has plural forms.
 */
class PluralLocalizableText internal constructor(
    private val quantity: Long,
    private val ruleType: PluralRuleType,
    key: Key,
    vararg args: Any?
) : AbstractLocalizableText(key, args) {
    override fun asString(locale: Locale): String {
        return MessageFormat(key.getPlural(locale).get(quantity, ruleType), locale).format(
            Text.localizeArgs(
                args,
                locale
            )
        )
    }

    /**
     * Builder for [PluralLocalizableText].
     * @see Text.plural
     */
	class Builder internal constructor() : LocalizableTextBuilder<Builder, PluralLocalizableText>() {
        private var quantity: Long? = null
        private var ruleType: PluralRuleType? = null

        /**
         * Sets the quantity used to determine the plural form.
         *
         * @param quantity integer quantity
         * @return this builder
         */
        fun quantity(quantity: Long): Builder {
            this.quantity = quantity
            return this
        }

		/**
		 * Sets the quantity used to determine the plural form.
		 *
		 * @param quantity integer quantity
		 * @return this builder
		 */
		fun quantity(quantity: Int): Builder {
			this.quantity = quantity.toLong()
			return this
		}

        /**
         * Sets the rule type used to determine the plural form.
         *
         * @param ruleType rule type
         * @return this builder
         */
        fun ruleType(ruleType: PluralRuleType): Builder {
            this.ruleType = ruleType
            return this
        }

        @Throws(IllegalStateException::class)
        override fun build(): PluralLocalizableText {
            checkNotNull(key) { "Translation key is not set" }
            checkNotNull(quantity) { "Quantity is not set" }
            checkNotNull(ruleType) { "Rule type is not set" }
            return PluralLocalizableText(quantity!!, ruleType!!, key!!, *args)
        }
    }
}
