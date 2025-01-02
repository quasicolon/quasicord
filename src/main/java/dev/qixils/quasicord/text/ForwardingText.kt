/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.text

import java.util.*

/**
 * A class that simply forwards all calls to another [Text] instance.
 */
interface ForwardingText : Text {

    /**
     * Returns the [Text] instance that this class forwards all calls to.
     *
     * @return delegated [Text] instance
     */
    val text: Text

    override fun asString(locale: Locale): String {
        return this.text.asString(locale)
    }
}
