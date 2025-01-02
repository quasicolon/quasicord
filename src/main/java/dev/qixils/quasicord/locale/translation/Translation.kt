/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale.translation

import java.util.*

/**
 * A translated object.
 */
interface Translation {

    /**
     * Gets the key of this translation.
     *
     * @return translation key
     */
    val key: String

    /**
     * Gets the locale of this translation.
     *
     * @return translation locale
     */
    val locale: Locale

    /**
     * Gets the originally requested locale for this translation.
     *
     *
     * This may be different from [.getLocale] if the requested locale was unavailable or
     * did not have a translation available for the requested key.
     *
     *
     * @return originally requested locale
     */
    val requestedLocale: Locale
}
