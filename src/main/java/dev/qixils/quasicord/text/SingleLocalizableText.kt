/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.text

import dev.qixils.quasicord.Key
import java.text.MessageFormat
import java.util.*
import kotlin.Throws

/**
 * Localizable text that has no plural forms.
 */
class SingleLocalizableText internal constructor(key: Key, vararg args: Any?) : AbstractLocalizableText(key, args) {
    override fun asString(locale: Locale): String {
        return MessageFormat(key.getSingle(locale).get(), locale).format(Text.localizeArgs(args, locale))
    }

    /**
     * Builder for [SingleLocalizableText].
     */
	class Builder internal constructor() : LocalizableTextBuilder<Builder, SingleLocalizableText>() {
        @Throws(IllegalStateException::class)
        override fun build(): SingleLocalizableText {
            checkNotNull(key) { "Translation key is not set" }
            return SingleLocalizableText(key!!, args)
        }
    }
}
