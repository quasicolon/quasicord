/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl.decorators.option;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes a pre-defined choice that a user may select for this {@link Option} in a
 * {@link dev.qixils.quasicolon.cogs.impl.decorators.slash.SlashCommand SlashCommand}.
 * <p>
 * While three different types of value methods are available for you to use, only the one matching the
 * {@link Option#type() type} specified in your {@link Option @Option} annotation will be utilized.
 * </p>
 * The name of this choice is taken from the translation file(s) using the {@link #id() provided ID}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Choices.class)
public @interface Choice {

	/**
	 * The ID of the choice in your translation file.
	 * <p>
	 * Should be lowercase and alphanumeric.
	 *
	 * @return choice ID
	 */
	String id();

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
