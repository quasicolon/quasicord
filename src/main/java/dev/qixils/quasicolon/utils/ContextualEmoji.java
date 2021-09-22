package dev.qixils.quasicolon.utils;

import dev.qixils.quasicolon.QuasicolonBot;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public final record ContextualEmoji(@NotNull String unicode, long emojiId) {
	@Nullable
	public Emote getEmote(@NotNull QuasicolonBot bot) {
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
		Emoji emoji = getEmoji(context);
		String name = emoji.getName();
		if (emoji.isCustom())
			name += ":" + emoji.getId();
		return name;
	}

	public static ContextualEmoji YES = new ContextualEmoji("\u2705", 328630479886614529L);
	public static ContextualEmoji NO = new ContextualEmoji("\u274C", 328630479576104963L);
}
