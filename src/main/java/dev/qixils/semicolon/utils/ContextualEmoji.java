package dev.qixils.semicolon.utils;

import dev.qixils.semicolon.Semicolon;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public record ContextualEmoji(@NotNull String unicode, long emojiId) {
	@Nullable
	public Emote getEmote(@NotNull Semicolon bot) {
		return getEmote(bot.getJda());
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

	public static ContextualEmoji YES = new ContextualEmoji("\u2705", 328630479886614529L);
	public static ContextualEmoji NO = new ContextualEmoji("\u274C", 328630479576104963L);
}
