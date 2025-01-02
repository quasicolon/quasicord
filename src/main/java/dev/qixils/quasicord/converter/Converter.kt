/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.converter

import net.dv8tion.jda.api.interactions.Interaction
import org.jetbrains.annotations.ApiStatus.NonExtendable

/**
 * An interface for converting a user-provided value to a different type.
 */
interface Converter<I, O> {

    /**
     * Returns the class of the input type.
     *
     * @return input class
     */
	val inputClass: Class<I>

    /**
     * Returns the class of the output type.
     *
     * @return output class
     */
	val outputClass: Class<O>

    /**
     * Converts an input to the output type.
     *
     * @param interaction the interaction being invoked
     * @param input       the user input
     * @param targetClass the class to convert to
     * @return converted value
     */
    fun convert(interaction: Interaction, input: I, targetClass: Class<out O>): O

    /**
     * Converts an input to the output type.
     *
     * @param interaction the interaction being invoked
     * @param input       the user input
     * @return converted value
     */
    @NonExtendable
    fun convert(interaction: Interaction, input: I): O {
        return convert(interaction, input, this.outputClass)
    }

    /**
     * Determines whether this converter can be converted to in a converter chain.
     *
     * @return true if this converter can be converted to
     */
	val canConvertTo: Boolean get() {
        return true
    }

    /**
     * Determines whether this converter can be converted from in a converter chain.
     *
     * @return true if this converter can be converted from
     */
    val canConvertFrom: Boolean get() {
        return true
    }
}
