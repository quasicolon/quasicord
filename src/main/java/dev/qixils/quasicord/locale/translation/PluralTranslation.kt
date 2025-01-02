/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale.translation

import net.xyzsd.plurals.PluralRuleType

/**
 * A translation which has multiple string literals
 * corresponding to the quantity of a specific argument.
 */
interface PluralTranslation : Translation {

    /**
     * Gets the translation corresponding to the given quantity.
     *
     * @param quantity the quantity to get the translation for
     * @param ruleType the rule type (ordinal or cardinal) to determine the plural form
     * @return the corresponding translation
     */
    fun get(quantity: Long, ruleType: PluralRuleType): String
}
