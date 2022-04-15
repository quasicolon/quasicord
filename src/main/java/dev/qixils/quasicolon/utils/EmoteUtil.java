/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.utils;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageReaction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class EmoteUtil {
    /**
     * Gets the string representation of the provided emote.
     * <p>
     * The string representation, as defined by the Discord API, is {@code emoteName:emoteId}.
     * For example: {@code greenTick:328630479886614529}
     * @param emote emote to get the string representation of
     * @return string representation
     */
    public static @NotNull String asString(@NotNull Emote emote) {
        Objects.requireNonNull(emote, "emote cannot be null");
        return emote.getName() + ":" + emote.getIdLong();
    }

    /**
     * Gets the string representation of the provided emoji.
     * <p>
     * For unicode emojis, this is the unicode characters.
     * </p>
     * For custom emotes, this is {@code emoteName:emoteId}.
     * For example: {@code greenTick:328630479886614529}
     * @param emoji emote to get the string representation of
     * @return string representation
     */
    public static @NotNull String asString(@NotNull Emoji emoji) {
        Objects.requireNonNull(emoji, "emote cannot be null");
        if (emoji.isUnicode())
            return emoji.getName();
        return emoji.getName() + ":" + emoji.getIdLong();
    }

    /**
     * Gets the string representation of the provided emoji.
     * <p>
     * For unicode emojis, this is the unicode characters.
     * </p>
     * For custom emotes, this is {@code emoteName:emoteId}.
     * For example: {@code greenTick:328630479886614529}
     * @param emoji emote to get the string representation of
     * @return string representation
     */
    public static @NotNull String asString(MessageReaction.@NotNull ReactionEmote emoji) {
        Objects.requireNonNull(emoji, "emote cannot be null");
        if (emoji.isEmoji())
            return emoji.getName();
        return emoji.getName() + ":" + emoji.getIdLong();
    }
}
