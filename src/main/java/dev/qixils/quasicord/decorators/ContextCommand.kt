/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.decorators;

import dev.qixils.quasicord.decorators.option.Contextual;
import dev.qixils.quasicord.decorators.slash.DefaultPermissions;
import dev.qixils.quasicord.decorators.slash.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.lang.annotation.*;

/**
 * Annotation for methods that represent context commands.
 * <p>
 * It is generally expected that the command have only one parameter representing the interaction or its subject,
 * i.e. a {@link net.dv8tion.jda.api.entities.User User} object
 * for a {@link net.dv8tion.jda.api.interactions.commands.context.UserContextInteraction UserContextInteraction}.
 * However, this is not a strict limit, and other parameters may be added if applicable, and will be treated similarly
 * to {@link Contextual} parameters in {@link SlashCommand}.
 * </p>
 * The name of this command is taken from the translation file(s) using the {@link #value() provided ID}.
 *
 * @see DefaultPermissions
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContextCommand {

	/**
	 * The ID of the command in your translation file.
	 * <p>
	 * Should be lowercase and alphanumeric (plus underscores).
	 * </p>
	 * See the Javadocs of {@link dev.qixils.quasicord.locale.TranslationProvider TranslationProvider}
	 * and {@link net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction LocalizationFunction}
	 * for more information on how to use this.
	 *
	 * @return command ID
	 */
	String value();

	/**
	 * The type of command.
	 * <p>
	 * Should not be {@link Command.Type#UNKNOWN} or {@link Command.Type#SLASH};
	 * see {@link SlashCommand} for slash commands.
	 */
	Command.Type type();

	/**
	 * Whether the command can only be used in guilds.
	 *
	 * @return whether the command can only be used in guilds
	 */
	boolean guildOnly() default false;

	/**
	 * Whether the command can only be used in age-restricted channels.
	 * Note that age-restricted commands cannot be included in discoverable apps and will not show up in DMs unless the
	 * user enables them in their settings.
	 *
	 * @return whether the command is age-restricted
	 */
	boolean ageRestricted() default false;
}
