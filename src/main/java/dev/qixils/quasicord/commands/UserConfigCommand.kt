/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.commands

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.reply_
import dev.qixils.quasicord.Key.Companion.library
import dev.qixils.quasicord.Quasicord
import dev.qixils.quasicord.autocomplete.impl.LocaleAutoCompleter
import dev.qixils.quasicord.autocomplete.impl.TimeZoneAutoCompleter
import dev.qixils.quasicord.db.collection.LocaleConfig
import dev.qixils.quasicord.db.collection.TimeZoneConfig
import dev.qixils.quasicord.decorators.Namespace
import dev.qixils.quasicord.decorators.option.AutoCompleteWith
import dev.qixils.quasicord.decorators.option.Contextual
import dev.qixils.quasicord.decorators.option.Option
import dev.qixils.quasicord.decorators.slash.SlashCommand
import dev.qixils.quasicord.decorators.slash.SlashSubCommand
import dev.qixils.quasicord.locale.Context
import dev.qixils.quasicord.text.Text
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

@Namespace("quasicord")
@SlashCommand("user-config")
class UserConfigCommand(library: Quasicord) : ConfigCommand(library) {
    @SlashSubCommand("language")
    suspend fun setLocaleCommand(
        @Option(
            value = "language",
            type = OptionType.STRING
        ) @AutoCompleteWith(LocaleAutoCompleter::class) locale: Locale?,
        @Contextual event: SlashCommandInteractionEvent,
		@Contextual ctx: Context,
    ) {
        setLocale(locale, LocaleConfig.EntryType.USER, event.user)
		val text = if (locale == null) ctx.text(library("user-config.language.output.removed"))
		else ctx.text(library("user-config.language.output.updated"), locale.getDisplayName(locale))
		event.reply_(text, ephemeral = event.isFromGuild).await()
    }

    @SlashSubCommand("timezone")
	suspend fun setTimeZoneCommand(
        @Option(
            value = "timezone",
            type = OptionType.STRING
        ) @AutoCompleteWith(TimeZoneAutoCompleter::class) tz: ZoneId?,
        @Contextual event: SlashCommandInteractionEvent,
		@Contextual ctx: Context,
    ) {
        val collection = library.databaseManager.collection<TimeZoneConfig>()
        val filter = Filters.eq("snowflake", event.user.idLong)
        if (tz == null) {
            collection.deleteOne(filter)
        } else {
            collection.replaceOne(
                filter,
                TimeZoneConfig(event.user.idLong, tz),
                ReplaceOptions().upsert(true)
            )
        }

		val text = if (tz == null) ctx.text(library("user-config.timezone.output.removed"))
		else ctx.text(library("user-config.timezone.output.updated"), Text { locale -> tz.getDisplayName(TextStyle.FULL_STANDALONE, locale) })
		event.reply_(text, ephemeral = event.isFromGuild).await()
    }
}
