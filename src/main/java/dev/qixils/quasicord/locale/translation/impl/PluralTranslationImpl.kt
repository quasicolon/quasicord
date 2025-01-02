/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale.translation.impl

import dev.qixils.quasicord.locale.translation.PluralTranslation
import net.xyzsd.plurals.PluralCategory
import net.xyzsd.plurals.PluralRule
import net.xyzsd.plurals.PluralRuleType
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Implementation of [PluralTranslation].
 */
class PluralTranslationImpl
/**
 * Initializes a new plural translation.
 *
 * @param key          the key of the translation
 * @param locale       the locale of the translation
 * @param translations the translations for each [PluralCategory]
 */(
    key: String,
    locale: Locale,
    requestedLocale: Locale,
    private val translations: MutableMap<PluralCategory, String>
) : AbstractTranslation(key, locale, requestedLocale), PluralTranslation {

    override fun get(quantity: Long, ruleType: PluralRuleType): String {
		val rule = PluralRule.createOrDefault(locale, ruleType)
		val category = rule.select(quantity.toLong())
        return translations[category] ?: run {
			logger.error("Plural translation missing for [locale={},quantity={},rule={},category={}]", locale, quantity, rule, category)
			key
		}
    }

    companion object {
		private val logger = LoggerFactory.getLogger("PluralTranslation")

        fun fromStringMap(
            key: String,
            locale: Locale,
            requestedLocale: Locale,
            translations: MutableMap<String?, String?>
        ): PluralTranslationImpl {
            val pluralTranslations: MutableMap<PluralCategory, String> = EnumMap(PluralCategory::class.java)
            for (entry in translations.entries) {
                pluralTranslations.put(PluralCategory.valueOf(entry.key!!.uppercase()), entry.value!!)
            }
            return PluralTranslationImpl(key, locale, requestedLocale, pluralTranslations)
        }
    }
}
