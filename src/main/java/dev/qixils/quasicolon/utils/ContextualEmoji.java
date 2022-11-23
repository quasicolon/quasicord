/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.utils;

import dev.qixils.quasicolon.Quasicord;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ContextualEmoji(@NotNull String unicode, long emojiId) {
	@Nullable
	public Emote getEmote(@NotNull Quasicord bot) {
		return getEmote(bot.getJDA());
	}

	@Nullable
	public Emote getEmote(@NotNull JDA jda) {
		return jda.getEmoteById(emojiId);
	}

	@NotNull
	public Emoji getEmoji(@NotNull MessageChannel context) {
		Emote emote = getEmote(context.getJDA());
		Emoji uniEmoji = Emoji.fromUnicode(unicode);
		if (emote == null)
			return uniEmoji;
		Emoji discEmoji = Emoji.fromEmote(emote);

		return PermissionUtil.canInteract(context.getJDA().getSelfUser(), emote, context)
				? discEmoji
				: uniEmoji;
	}

	@NotNull
	public String getEmojiString(@NotNull MessageChannel context) {
		return EmoteUtil.asString(getEmoji(context));
	}

	public static final ContextualEmoji YES = new ContextualEmoji("\u2705", 328630479886614529L);
	public static final ContextualEmoji NO = new ContextualEmoji("\u274C", 328630479576104963L);
}
