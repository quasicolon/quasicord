/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.decorators.slash;

import dev.qixils.quasicord.decorators.option.Contextual;
import dev.qixils.quasicord.decorators.option.Option;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods that represent slash sub-commands.
 * <p>
 * This method's parameters should all be annotated either with
 * {@link Option Option} or
 * {@link Contextual Contextual}.
 * </p>
 * The name and description of this command are taken from the translation file(s)
 * using the {@link #value() provided ID}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlashSubCommand {

	/**
	 * The ID of the command in your translation file.
	 * <p>
	 * Should be lowercase and alphanumeric.
	 * </p>
	 * Dots indicate a subcommand group, i.e. "pronouns.add".
	 * Note that the parent of a subcommand group cannot itself be a command,
	 * i.e. you cannot have commands both for "pronouns" and "pronouns.add".
	 * <p>
	 * See the Javadocs of {@link dev.qixils.quasicord.locale.TranslationProvider TranslationProvider}
	 * and {@link net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction LocalizationFunction}
	 * for more information on how to use this.
	 *
	 * @return command ID
	 */
	String value();
}
