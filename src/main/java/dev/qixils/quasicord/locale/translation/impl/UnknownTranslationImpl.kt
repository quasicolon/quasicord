/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale.translation.impl

import dev.qixils.quasicord.locale.translation.UnknownTranslation
import net.xyzsd.plurals.PluralRuleType
import java.util.*

/**
 * An implementation of [UnknownTranslation].
 */
class UnknownTranslationImpl
/**
 * Initializes a new unknown translation.
 *
 * @param key             the key of the translation
 * @param requestedLocale the requested locale
 */
    (
    key: String,
    requestedLocale: Locale
) : AbstractTranslation(key, Locale.ROOT, requestedLocale), UnknownTranslation {

	override fun get(): String {
		return key
	}

	override fun get(quantity: Long, ruleType: PluralRuleType): String {
		return key
	}
}
