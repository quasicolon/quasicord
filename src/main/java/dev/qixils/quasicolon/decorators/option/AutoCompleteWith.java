/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators.option;

import dev.qixils.quasicolon.decorators.functional.AutoCompleter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes the class that should be used to generate tab completions for a {@link Option}.
 * This is only to be used by options whose {@link Option#type() type}
 * {@link net.dv8tion.jda.api.interactions.commands.OptionType#canSupportChoices() supports choices}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoCompleteWith {

	/**
	 * The class to use for generating auto-complete suggestions.
	 *
	 * @return auto-completer class
	 */
	Class<? extends AutoCompleter> value();
}
