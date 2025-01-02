/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators.option

/**
 * Denotes the range of values allowed for a [Option] of [type][Option.type]
 * [NUMBER][net.dv8tion.jda.api.interactions.commands.OptionType.NUMBER],
 * [INTEGER][net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER], or
 * [STRING][net.dv8tion.jda.api.interactions.commands.OptionType.STRING] (length).
 *
 * @see Option
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Range(

    /**
     * The minimum value allowed. Defaults to [Double.NaN] (undefined).
     * For numbers, this must be within
     * [[MIN_NEGATIVE_NUMBER][net.dv8tion.jda.api.interactions.commands.build.OptionData.MIN_NEGATIVE_NUMBER],
     * [MAX_POSITIVE_NUMBER][net.dv8tion.jda.api.interactions.commands.build.OptionData.MAX_POSITIVE_NUMBER]].
     * For strings, this is the minimum length and must be within
     * [0, {@value net.dv8tion.jda.api.interactions.commands.build.OptionData#MAX_STRING_OPTION_LENGTH}].
     *
     * @return minimum value
     */
    val min: Double = Double.Companion.NaN,

	/**
     * The maximum value allowed. Defaults to [Double.NaN] (undefined).
     * For numbers, this must be within
     * [[MIN_NEGATIVE_NUMBER][net.dv8tion.jda.api.interactions.commands.build.OptionData.MIN_NEGATIVE_NUMBER],
     * [MAX_POSITIVE_NUMBER][net.dv8tion.jda.api.interactions.commands.build.OptionData.MAX_POSITIVE_NUMBER]].
     * For strings, this is the maximum length and must be within
     * [1, {@value net.dv8tion.jda.api.interactions.commands.build.OptionData#MAX_STRING_OPTION_LENGTH}].
     *
     * @return maximum value
     */
    val max: Double = Double.Companion.NaN
)
