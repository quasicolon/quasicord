/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl.decorators.option;

import dev.qixils.quasicolon.autocomplete.AutoCompleter;

/**
 * Denotes the class that should be used to generate tab completions for a {@link Option}.
 * This is only to be used by options whose {@link Option#type() type}
 * {@link net.dv8tion.jda.api.interactions.commands.OptionType#canSupportChoices() supports choices}.
 */
public @interface AutoCompleting {

	/**
	 * The class to use for generating auto-complete suggestions.
	 *
	 * @return auto-completer class
	 */
	Class<? extends AutoCompleter> value();
}
