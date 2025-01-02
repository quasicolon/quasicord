/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators

import net.dv8tion.jda.api.interactions.commands.Command
import java.lang.annotation.Inherited

/**
 * Annotation for methods that represent context commands.
 *
 *
 * It is generally expected that the command have only one parameter representing the interaction or its subject,
 * i.e. a [User][net.dv8tion.jda.api.entities.User] object
 * for a [UserContextInteraction][net.dv8tion.jda.api.interactions.commands.context.UserContextInteraction].
 * However, this is not a strict limit, and other parameters may be added if applicable, and will be treated similarly
 * to [dev.qixils.quasicord.decorators.option.Contextual] parameters in [dev.qixils.quasicord.decorators.slash.SlashCommand].
 *
 * The name of this command is taken from the translation file(s) using the [provided ID][.value].
 *
 * @see dev.qixils.quasicord.decorators.slash.DefaultPermissions
 */
@Inherited
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ContextCommand(

    /**
     * The ID of the command in your translation file.
     *
     *
     * Should be lowercase and alphanumeric (plus underscores).
     *
     * See the Javadocs of [TranslationProvider][dev.qixils.quasicord.locale.TranslationProvider]
     * and [LocalizationFunction][net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction]
     * for more information on how to use this.
     *
     * @return command ID
     */
    val value: String,

    /**
     * The type of command.
     *
     *
     * Should not be [Command.Type.UNKNOWN] or [Command.Type.SLASH];
     * see [dev.qixils.quasicord.decorators.slash.SlashCommand] for slash commands.
     */
    val type: Command.Type,

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
