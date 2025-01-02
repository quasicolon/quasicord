/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.commands;

import dev.qixils.quasicord.Key;
import dev.qixils.quasicord.Quasicord;
import dev.qixils.quasicord.autocomplete.impl.LocaleAutoCompleter;
import dev.qixils.quasicord.db.collection.LocaleConfig;
import dev.qixils.quasicord.decorators.Namespace;
import dev.qixils.quasicord.decorators.option.AutoCompleteWith;
import dev.qixils.quasicord.decorators.option.Contextual;
import dev.qixils.quasicord.decorators.option.Option;
import dev.qixils.quasicord.decorators.slash.DefaultPermissions;
import dev.qixils.quasicord.decorators.slash.SlashCommand;
import dev.qixils.quasicord.decorators.slash.SlashSubCommand;
import dev.qixils.quasicord.text.Text;
import dev.qixils.quasicord.utils.QuasiMessage;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.checkerframework.checker.nullness.qual.NonNull;
import reactor.core.publisher.Mono;

import java.util.Locale;

@Namespace("quasicord")
@SlashCommand(value = "server-config", guildOnly = true)
@DefaultPermissions({Permission.MANAGE_SERVER})
public class GuildConfigCommand extends ConfigCommand {

	public GuildConfigCommand(@NonNull Quasicord library) {
		super(library);
	}

	@SlashSubCommand("language")
	public Mono<QuasiMessage> setLocaleCommand(
		@Option(value = "language", type = OptionType.STRING)
		@AutoCompleteWith(LocaleAutoCompleter.class)
		Locale locale,
		@Contextual
		Guild guild
	) {
		return setLocale(locale, LocaleConfig.EntryType.GUILD, guild).map($ -> locale == null
			? Text.single(Key.library("guild-config.language.output.removed"))
			: Text.single(Key.library("guild-config.language.output.updated"), locale.getDisplayName(locale)))
			.map(text -> new QuasiMessage(text, request -> {
				if (request instanceof ReplyCallbackAction action) {
					//noinspection ResultOfMethodCallIgnored
					action.setEphemeral(true);
				}
			}));
	}

}
