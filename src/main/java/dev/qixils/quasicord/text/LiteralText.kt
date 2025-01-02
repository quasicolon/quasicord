/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.text

import java.util.*

@JvmRecord
internal data class LiteralText(val text: String) : Text {
    override fun asString(locale: Locale): String {
        return text
    }
}
