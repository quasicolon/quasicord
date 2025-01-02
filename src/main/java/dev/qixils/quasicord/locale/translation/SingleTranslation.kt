/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale.translation

/**
 * A loaded translation with no plural forms.
 */
interface SingleTranslation : Translation {

    /**
     * Gets the translated string.
     *
     * @return translated string
     */
    fun get(): String
}
