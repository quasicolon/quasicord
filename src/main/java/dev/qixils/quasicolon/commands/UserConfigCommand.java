/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.commands;

import dev.qixils.quasicolon.Quasicord;
import dev.qixils.quasicolon.autocomplete.impl.LocaleAutoCompleter;
import dev.qixils.quasicolon.decorators.Namespace;
import dev.qixils.quasicolon.decorators.option.AutoCompleteWith;
import dev.qixils.quasicolon.decorators.option.Option;
import dev.qixils.quasicolon.decorators.slash.SlashCommand;
import dev.qixils.quasicolon.decorators.slash.SlashSubCommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Locale;

@Namespace("quasicord")
@SlashCommand("user-config")
public class UserConfigCommand {

	protected final Quasicord library;

	public UserConfigCommand(Quasicord library) {
		this.library = library;
	}

	@SlashSubCommand("language")
	public void setLocale(
		@Option(value = "language", type = OptionType.STRING)
		@AutoCompleteWith(LocaleAutoCompleter.class)
		Locale locale
	) {
		// TODO: handle diacritics
		// TODO: handle null ? idk i dont think so
		// TODO: save to DB (add some Mono<Void> setter to LocaleConverter maybe)
	}

	// TODO: probably remove per-channel locale config i think
	// TODO: timezone command
}
