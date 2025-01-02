/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.utils

import dev.qixils.quasicord.text.Text
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest
import java.util.function.Consumer

/**
 * Wrapper that stores [Text] to send to a channel and a modifier
 * which alters the [MessageCreateAction] created when sending the message.
 *
 *
 * This wrapper is useful for sending embeds or attachments
 * from a [SlashCommand][dev.qixils.quasicord.cogs.SlashCommand] method.
 */
@JvmRecord
data class QuasiMessage(val text: Text, val modifier: Consumer<MessageCreateRequest<*>?>)
