/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators.option;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes the range of values allowed for a {@link Option} of {@link Option#type() type}
 * {@link net.dv8tion.jda.api.interactions.commands.OptionType#NUMBER NUMBER},
 * {@link net.dv8tion.jda.api.interactions.commands.OptionType#INTEGER INTEGER}, or
 * {@link net.dv8tion.jda.api.interactions.commands.OptionType#STRING STRING} (length).
 *
 * @see Option
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {

	/**
	 * The minimum value allowed. Defaults to {@link Double#NaN} (undefined).
	 * For numbers, this must be within
	 * [{@link net.dv8tion.jda.api.interactions.commands.build.OptionData#MIN_NEGATIVE_NUMBER MIN_NEGATIVE_NUMBER},
	 * {@link net.dv8tion.jda.api.interactions.commands.build.OptionData#MAX_POSITIVE_NUMBER MAX_POSITIVE_NUMBER}].
	 * For strings, this is the minimum length and must be within
	 * [0, {@value net.dv8tion.jda.api.interactions.commands.build.OptionData#MAX_STRING_OPTION_LENGTH}].
	 *
	 * @return minimum value
	 */
	double min() default Double.NaN;

	/**
	 * The maximum value allowed. Defaults to {@link Double#NaN} (undefined).
	 * For numbers, this must be within
	 * [{@link net.dv8tion.jda.api.interactions.commands.build.OptionData#MIN_NEGATIVE_NUMBER MIN_NEGATIVE_NUMBER},
	 * {@link net.dv8tion.jda.api.interactions.commands.build.OptionData#MAX_POSITIVE_NUMBER MAX_POSITIVE_NUMBER}].
	 * For strings, this is the maximum length and must be within
	 * [1, {@value net.dv8tion.jda.api.interactions.commands.build.OptionData#MAX_STRING_OPTION_LENGTH}].
	 *
	 * @return maximum value
	 */
	double max() default Double.NaN;
}
