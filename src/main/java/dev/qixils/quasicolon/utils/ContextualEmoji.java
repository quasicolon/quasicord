/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.utils;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.jetbrains.annotations.NotNull;

public record ContextualEmoji(@NotNull String unicode, long emojiId) {
	@NotNull
	public Emoji getEmoji(@NotNull MessageChannel context) {
		RichCustomEmoji discEmoji = context.getJDA().getEmojiById(emojiId);
		Emoji uniEmoji = Emoji.fromUnicode(unicode);
		if (discEmoji == null)
			return uniEmoji;

		return PermissionUtil.canInteract(context.getJDA().getSelfUser(), discEmoji, context)
				? discEmoji
				: uniEmoji;
	}

	public static final ContextualEmoji YES = new ContextualEmoji("\u2705", 328630479886614529L);
	public static final ContextualEmoji NO = new ContextualEmoji("\u274C", 328630479576104963L);
}
