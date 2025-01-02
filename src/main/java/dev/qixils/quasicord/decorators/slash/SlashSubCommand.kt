/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators.slash

/**
 * Annotation for methods that represent slash sub-commands.
 *
 *
 * This method's parameters should all be annotated either with
 * [dev.qixils.quasicord.decorators.option.Option] or
 * [dev.qixils.quasicord.decorators.option.Contextual].
 *
 * The name and description of this command are taken from the translation file(s)
 * using the [provided ID][value].
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class SlashSubCommand(

    /**
     * The ID of the command in your translation file.
     *
     *
     * Should be lowercase and alphanumeric.
     *
     * Dots indicate a subcommand group, i.e. "pronouns.add".
     * Note that the parent of a subcommand group cannot itself be a command,
     * i.e. you cannot have commands both for "pronouns" and "pronouns.add".
     *
     *
     * See the Javadocs of [TranslationProvider][dev.qixils.quasicord.locale.TranslationProvider]
     * and [LocalizationFunction][net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction]
     * for more information on how to use this.
     *
     * @return command ID
     */
    val value: String
)
