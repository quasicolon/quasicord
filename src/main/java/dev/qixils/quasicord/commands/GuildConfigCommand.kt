/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.commands

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.reply_
import dev.qixils.quasicord.Key.Companion.library
import dev.qixils.quasicord.Quasicord
import dev.qixils.quasicord.autocomplete.impl.LocaleAutoCompleter
import dev.qixils.quasicord.db.collection.LocaleConfig
import dev.qixils.quasicord.decorators.Namespace
import dev.qixils.quasicord.decorators.option.AutoCompleteWith
import dev.qixils.quasicord.decorators.option.Contextual
import dev.qixils.quasicord.decorators.option.Option
import dev.qixils.quasicord.decorators.slash.DefaultPermissions
import dev.qixils.quasicord.decorators.slash.SlashCommand
import dev.qixils.quasicord.decorators.slash.SlashSubCommand
import dev.qixils.quasicord.locale.Context
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.*

@Namespace("quasicord")
@SlashCommand(value = "server-config", guildOnly = true)
@DefaultPermissions(Permission.MANAGE_SERVER)
class GuildConfigCommand(library: Quasicord) : ConfigCommand(library) {
    @SlashSubCommand("language")
    suspend fun setLocaleCommand(
        @Option(
            value = "language",
            type = OptionType.STRING
        ) @AutoCompleteWith(LocaleAutoCompleter::class) locale: Locale?,
		@Contextual event: SlashCommandInteractionEvent,
		@Contextual ctx: Context,
    ) {
        setLocale(locale, LocaleConfig.EntryType.GUILD, event.guild!!)
		val text = if (locale == null) ctx.text(library("guild-config.language.output.removed"))
		else ctx.text(library("guild-config.language.output.updated"), locale.getDisplayName(locale))
		event.reply_(text, ephemeral = true).await()
    }
}
