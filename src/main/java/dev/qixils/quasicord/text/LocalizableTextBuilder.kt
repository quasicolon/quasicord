/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.text

import dev.qixils.quasicord.Builder
import dev.qixils.quasicord.Key
import org.jetbrains.annotations.Contract

/**
 * A generic builder for creating [LocalizableText] instances.
 */
@Suppress("UNCHECKED_CAST")
abstract class LocalizableTextBuilder<B : LocalizableTextBuilder<B, R>, R : LocalizableText> : Builder<R> {
    @JvmField
	protected var key: Key? = null
    @JvmField
	protected var args: Array<out Any?> = arrayOf<Any?>()

    /**
     * Sets the translation key.
     *
     * @param key translation key
     * @return this builder
     */
    @Contract("_ -> this")
    fun key(key: Key): B {
        this.key = key
        return this as B
    }

    /**
     * Sets the arguments used to format the translated string.
     *
     * @param args arguments
     * @return this builder
     */
    @Contract("_ -> this")
    fun args(vararg args: Any?): B {
        this.args = args
        return this as B
    }
}
