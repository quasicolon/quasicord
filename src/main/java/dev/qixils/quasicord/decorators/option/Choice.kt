/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators.option

/**
 * Denotes a pre-defined choice that a user may select for this [Option] in a
 * [dev.qixils.quasicord.cogs.SlashCommand].
 *
 *
 * While three different types of value methods are available for you to use, only the one matching the
 * [type][Option.type] specified in your [@Option][Option] annotation will be utilized.
 *
 * The name of this choice is taken from the translation file(s) using the [provided ID][.value].
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@JvmRepeatable(Choices::class)
annotation class Choice(

    /**
     * The ID of the choice in your translation file.
     *
     *
     * Should be lowercase and alphanumeric.
     *
     * See the Javadocs of [TranslationProvider][dev.qixils.quasicord.locale.TranslationProvider]
     * and [LocalizationFunction][net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction]
     * for more information on how to use this.
     *
     * @return choice ID
     */
    val value: String,

    /**
     * The value of an [INTEGER][net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER] choice.
     *
     * @return integer value
     */
    val intValue: Long = 0,

    /**
     * The value of a [NUMBER][net.dv8tion.jda.api.interactions.commands.OptionType.NUMBER] choice.
     *
     * @return number value
     */
    val numberValue: Double = Double.Companion.NaN,

    /**
     * The value of a [STRING][net.dv8tion.jda.api.interactions.commands.OptionType.STRING] choice.
     *
     * @return string value
     */
    val stringValue: String = ""
)
