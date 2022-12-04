/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl.decorators.jda;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods that represent interaction commands.
 * <p>
 * The name of this command is taken from the translation file(s)
 * using the {@link #value() provided ID} (and, optionally, the {@link SlashCommandGroup}).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationCommand {

	/**
	 * The ID of the command in your translation file.
	 * <p>
	 * Should be lowercase and alphanumeric (plus underscores).
	 * </p>
	 * Forward slashes indicate a subcommand group, i.e. "pronouns/add".
	 * Note that the parent of a subcommand group cannot itself be a command,
	 * i.e. you cannot have commands both for "pronouns" and "pronouns/add".
	 *
	 * @return command ID
	 */
	@NonNull String value();

	/**
	 * The type of command.
	 * <p>
	 * Should not be {@link Command.Type#UNKNOWN} or {@link Command.Type#SLASH};
	 * see {@link SlashCommand} for slash commands.
	 */
	Command.@NonNull Type type();

	/**
	 * The permissions required to use this command by default.
	 * Note that moderators can override these.
	 *
	 * @return default permissions
	 */
	Permission @NonNull [] defaultPermissions() default {};

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
