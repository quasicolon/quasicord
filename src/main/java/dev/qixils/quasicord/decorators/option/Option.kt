/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators.option

import net.dv8tion.jda.api.interactions.commands.OptionType

/**
 * Annotation for parameters that represent
 * [slash command][dev.qixils.quasicord.cogs.SlashCommand]
 * arguments (options).
 *
 *
 * The name and description of this option are taken from the translation file(s)
 * using the [provided ID][.value].
 *
 * @see ChannelTypes
 *
 * @see Range
 *
 * @see AutoCompleteWith
 *
 * @see Choice
 *
 * @see ConvertWith
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Option(

	/**
     * The ID of the option in your translation file.
     *
     *
     * Should be lowercase and alphanumeric.
     *
     * See the Javadocs of [TranslationProvider][dev.qixils.quasicord.locale.TranslationProvider]
     * and [LocalizationFunction][net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction]
     * for more information on how to use this.
     *
     * @return option ID
     */
    val value: String,

    /**
     * Whether this option is required.
     * Defaults to `true`.
     *
     * @return whether this option is required
     */
    val required: Boolean = true,

	/**
     * The [type][OptionType] of this option.
     * This determines how the [ConverterRegistry][dev.qixils.quasicord.converter.ConverterRegistry] will attempt
     * to convert the user-provided value to the parameter type.
     * If unset, the type will attempt to be inferred from the parameter type.
     *
     * @return the type of this option
     */
    val type: OptionType = OptionType.UNKNOWN
)
