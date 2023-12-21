/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.decorators.option;

import dev.qixils.quasicord.decorators.slash.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: annotation denoting a custom class for option parsing

/**
 * Annotation for parameters that represent
 * {@link SlashCommand slash command}
 * arguments (options).
 * <p>
 * The name and description of this option are taken from the translation file(s)
 * using the {@link #value() provided ID}.
 *
 * @see ChannelTypes
 * @see Range
 * @see AutoCompleteWith
 * @see Choice
 * @see ConvertWith
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {

	/**
	 * The ID of the option in your translation file.
	 * <p>
	 * Should be lowercase and alphanumeric.
	 * </p>
	 * See the Javadocs of {@link dev.qixils.quasicord.locale.TranslationProvider TranslationProvider}
	 * and {@link net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction LocalizationFunction}
	 * for more information on how to use this.
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
	 * This determines how the {@link dev.qixils.quasicord.converter.ConverterRegistry ConverterRegistry} will attempt
	 * to convert the user-provided value to the parameter type.
	 * If unset, the type will attempt to be inferred from the parameter type.
	 *
	 * @return the type of this option
	 */
	OptionType type() default OptionType.UNKNOWN;
}
