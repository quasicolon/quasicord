/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators.slash

import java.lang.annotation.Inherited

/**
 * Annotation for methods that represent slash commands.
 *
 *
 * This method's parameters should all be annotated either with
 * [Option] or
 * [Contextual].
 *
 * The name and description of this command are taken from the translation file(s)
 * using the [provided ID][.value].
 *
 * @see DefaultPermissions
 */
@Inherited
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.RUNTIME)
annotation class SlashCommand(

    /**
     * The ID of the command in your translation file.
     *
     *
     * Should be lowercase and alphanumeric.
     *
     *
     * See the Javadocs of [TranslationProvider][dev.qixils.quasicord.locale.TranslationProvider]
     * and [LocalizationFunction][net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction]
     * for more information on how to use this.
     *
     * @return command ID
     */
    val value: String,

    /**
     * Whether the command can only be used in guilds.
     *
     * @return whether the command can only be used in guilds
     */
    val guildOnly: Boolean = false,

    /**
     * Whether the command can only be used in age-restricted channels.
     * Note that age-restricted commands cannot be included in discoverable apps and will not show up in DMs unless the
     * user enables them in their settings.
     *
     * @return whether the command is age-restricted
     */
    val ageRestricted: Boolean = false
)
