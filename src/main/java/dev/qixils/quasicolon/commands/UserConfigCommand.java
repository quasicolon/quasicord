/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.commands;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.Quasicord;
import dev.qixils.quasicolon.autocomplete.impl.LocaleAutoCompleter;
import dev.qixils.quasicolon.decorators.Namespace;
import dev.qixils.quasicolon.decorators.option.AutoCompleteWith;
import dev.qixils.quasicolon.decorators.option.Contextual;
import dev.qixils.quasicolon.decorators.option.Option;
import dev.qixils.quasicolon.decorators.slash.SlashCommand;
import dev.qixils.quasicolon.decorators.slash.SlashSubCommand;
import dev.qixils.quasicolon.locale.LocaleConfig;
import dev.qixils.quasicolon.text.Text;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import reactor.core.publisher.Mono;

import java.util.Locale;

@Namespace("quasicord")
@SlashCommand("user-config")
public class UserConfigCommand extends ConfigCommand {

	public UserConfigCommand(Quasicord library) {
		super(library);
	}

	@SlashSubCommand("language")
	public Mono<Text> setLocaleCommand(
		@Option(value = "language", type = OptionType.STRING)
		@AutoCompleteWith(LocaleAutoCompleter.class)
		Locale locale,
		@Contextual
		User user
	) {
		return setLocale(locale, LocaleConfig.EntryType.USER, user).map($ -> locale == null
			? Text.single(Key.library("user-config.language.output.removed"))
			: Text.single(Key.library("user-config.language.output.updated"), locale.getDisplayName(locale)));
	}


	// TODO: timezone command
}
