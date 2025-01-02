/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.decorators.option;

import dev.qixils.quasicord.decorators.slash.SlashCommand;

import java.lang.annotation.*;

/**
 * Denotes a pre-defined choice that a user may select for this {@link Option} in a
 * {@link SlashCommand SlashCommand}.
 * <p>
 * While three different types of value methods are available for you to use, only the one matching the
 * {@link Option#type() type} specified in your {@link Option @Option} annotation will be utilized.
 * </p>
 * The name of this choice is taken from the translation file(s) using the {@link #value() provided ID}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Choices.class)
public @interface Choice {

	/**
	 * The ID of the choice in your translation file.
	 * <p>
	 * Should be lowercase and alphanumeric.
	 * </p>
	 * See the Javadocs of {@link dev.qixils.quasicord.locale.TranslationProvider TranslationProvider}
	 * and {@link net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction LocalizationFunction}
	 * for more information on how to use this.
	 *
	 * @return choice ID
	 */
	String value();

	/**
	 * The value of an {@link net.dv8tion.jda.api.interactions.commands.OptionType#INTEGER INTEGER} choice.
	 *
	 * @return integer value
	 */
	long intValue() default 0;

	/**
	 * The value of a {@link net.dv8tion.jda.api.interactions.commands.OptionType#NUMBER NUMBER} choice.
	 *
	 * @return number value
	 */
	double numberValue() default Double.NaN;

	/**
	 * The value of a {@link net.dv8tion.jda.api.interactions.commands.OptionType#STRING STRING} choice.
	 *
	 * @return string value
	 */
	String stringValue() default "";
}
