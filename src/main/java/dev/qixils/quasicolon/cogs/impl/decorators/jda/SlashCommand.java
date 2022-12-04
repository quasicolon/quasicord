/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl.decorators.jda;

import dev.qixils.quasicolon.autocomplete.AutoCompleter;
import dev.qixils.quasicolon.autocomplete.StaticAutoCompleter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods that represent slash commands.
 * <p>
 * This command's parameters should all be annotated either with {@link Option} or {@link Contextual}.
 * </p>
 * The name and description of this command are taken from the translation file(s)
 * using the {@link #value() provided ID}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlashCommand {

	/**
	 * The ID of the command in your translation file.
	 * <p>
	 * Should be lowercase and alphanumeric.
	 *
	 * @return command ID
	 */
	String value();

	/**
	 * The permissions required to use this command by default.
	 * Note that moderators can override these.
	 *
	 * @return default permissions
	 */
	// todo: move to own annotation
	Permission[] defaultPermissions() default {};

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

	/**
	 * Annotation for parameters that represent slash command arguments (options).
	 * <p>
	 * The name and description of this option are taken from the translation file(s)
	 * using the {@link #value() provided ID}.
	 */
	@Target(ElementType.PARAMETER)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Option {

		/**
		 * The ID of the option in your translation file.
		 * <p>
		 * Should be lowercase and alphanumeric.
		 *
		 * @return option ID
		 */
		String value();

		/**
		 * Whether this option is required.
		 * Defaults to {@code true}.
		 *
		 * @return whether this option is required
		 */
		boolean required() default true;

		/**
		 * The {@link OptionType type} of this option.
		 * If unset, the type will attempt to be inferred from the parameter type.
		 *
		 * @return the type of this option
		 */
		OptionType type() default OptionType.UNKNOWN;

		/**
		 * The {@link ChannelType ChannelType}s this option is restricted to.
		 * Only applicable when {@link #type()} is {@link OptionType#CHANNEL CHANNEL}.
		 *
		 * @return supported channel types
		 */
		// TODO: move to own annotation
		ChannelType[] channelTypes() default {};

		/**
		 * The class to use for generating auto-complete suggestions.
		 *
		 * @return auto-completer class
		 */
		// TODO: remove static auto completer, move to own annotation
		Class<? extends AutoCompleter> completer() default StaticAutoCompleter.class;

		// TODO: Range annotation for INTEGER, NUMBER, and STRING
		// TODO: @Repeatable annotation for Command.Choice
	}
}
