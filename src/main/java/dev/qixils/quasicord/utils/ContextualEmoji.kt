/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.utils

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.internal.utils.PermissionUtil

@JvmRecord
data class ContextualEmoji(val unicode: String, val emojiId: Long) {
    fun getEmoji(context: MessageChannel): Emoji {
        val discEmoji = context.jda.getEmojiById(emojiId)
        val uniEmoji: Emoji = Emoji.fromUnicode(unicode)
        if (discEmoji == null) return uniEmoji

        return if (PermissionUtil.canInteract(context.jda.selfUser, discEmoji, context))
            discEmoji
        else
            uniEmoji
    }

    companion object {
        val YES: ContextualEmoji = ContextualEmoji("\u2705", 328630479886614529L)
        val NO: ContextualEmoji = ContextualEmoji("\u274C", 328630479576104963L)
    }
}
