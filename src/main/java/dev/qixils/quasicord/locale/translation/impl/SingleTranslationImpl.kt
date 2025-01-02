/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale.translation.impl

import dev.qixils.quasicord.locale.translation.SingleTranslation
import java.util.*

/**
 * Implementation of [SingleTranslation].
 */
class SingleTranslationImpl(
    key: String,
    locale: Locale,
    requestedLocale: Locale,
    private val translation: String
) : AbstractTranslation(key, locale, requestedLocale), SingleTranslation {

    override fun get(): String {
        return translation
    }
}
