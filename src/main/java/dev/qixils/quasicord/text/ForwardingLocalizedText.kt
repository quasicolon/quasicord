/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.text

import dev.qixils.quasicord.Key


/**
 * A class that simply forwards all calls to another [LocalizableText] instance.
 */
interface ForwardingLocalizedText : ForwardingText, LocalizableText {

    /**
     * Returns the [LocalizableText] instance that this class forwards all calls to.
     *
     * @return delegated [LocalizableText] instance
     */
	override val text: LocalizableText

    override val key: Key
        get() = text.key

    override val args: Array<out Any?>
        get() = text.args
}
