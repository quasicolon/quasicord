/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.commands;

import com.mongodb.client.model.ReplaceOptions;
import dev.qixils.quasicord.Key;
import dev.qixils.quasicord.Quasicord;
import dev.qixils.quasicord.autocomplete.impl.LocaleAutoCompleter;
import dev.qixils.quasicord.autocomplete.impl.TimeZoneAutoCompleter;
import dev.qixils.quasicord.db.collection.LocaleConfig;
import dev.qixils.quasicord.db.collection.TimeZoneConfig;
import dev.qixils.quasicord.decorators.Namespace;
import dev.qixils.quasicord.decorators.option.AutoCompleteWith;
import dev.qixils.quasicord.decorators.option.Contextual;
import dev.qixils.quasicord.decorators.option.Option;
import dev.qixils.quasicord.decorators.slash.SlashCommand;
import dev.qixils.quasicord.decorators.slash.SlashSubCommand;
import dev.qixils.quasicord.text.Text;
import dev.qixils.quasicord.utils.QuasiMessage;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.mongodb.client.model.Filters.eq;

@Namespace("quasicord")
@SlashCommand("user-config")
public class UserConfigCommand extends ConfigCommand {

	public UserConfigCommand(Quasicord library) {
		super(library);
	}

	@SlashSubCommand("language")
	public Mono<QuasiMessage> setLocaleCommand(
		@Option(value = "language", type = OptionType.STRING)
		@AutoCompleteWith(LocaleAutoCompleter.class)
		Locale locale,
		@Contextual
		User user,
		@Contextual
		Channel channel
	) {
		return setLocale(locale, LocaleConfig.EntryType.USER, user).map($ -> locale == null
			? Text.single(Key.library("user-config.language.output.removed"))
			: Text.single(Key.library("user-config.language.output.updated"), locale.getDisplayName(locale)))
			.map(text -> new QuasiMessage(text, request -> {
				if (request instanceof ReplyCallbackAction action && channel.getType() != ChannelType.PRIVATE) {
					//noinspection ResultOfMethodCallIgnored
					action.setEphemeral(true);
				}
			}));
	}

	@SlashSubCommand("timezone")
	public Mono<QuasiMessage> setTimeZoneCommand(
		@Option(value = "timezone", type = OptionType.STRING)
		@AutoCompleteWith(TimeZoneAutoCompleter.class)
		ZoneId tz,
		@Contextual
		User user,
		@Contextual
		Channel channel
	) {
		var collection = library.getDatabaseManager().collection(TimeZoneConfig.class);
		var filter = eq("id", user.getIdLong());
		Publisher<?> result;
		if (tz == null) {
			result = collection.deleteOne(filter);
		} else {
			result = collection.replaceOne(
				filter,
				new TimeZoneConfig(user.getIdLong(), tz),
				new ReplaceOptions().upsert(true)
			);
		}
		return Mono.from(result).map($ -> tz == null
			? Text.single(Key.library("user-config.timezone.output.removed"))
			: Text.single(Key.library("user-config.timezone.output.updated"), (Text) locale -> tz.getDisplayName(TextStyle.FULL_STANDALONE, locale)))
			.map(text -> new QuasiMessage(text, request -> {
				if (request instanceof ReplyCallbackAction action && channel.getType() != ChannelType.PRIVATE) {
					//noinspection ResultOfMethodCallIgnored
					action.setEphemeral(true);
				}
			}));
	}
}
