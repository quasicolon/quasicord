package dev.qixils.semicolon.variables.parsers.snowflakes;

import dev.qixils.semicolon.Semicolon;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class EmoteParser extends SnowflakeParser<Emote> {
    public EmoteParser(Semicolon bot) {
        super(bot);
    }

    @Override
    public @Nullable Emote fromDatabase(@NotNull String value) {
        return bot.getJda().getEmoteById(value);
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Emote> parseText(@NotNull Message context, @NotNull String humanText) {
        return super.parseText(context, humanText).thenApply(superEmote -> {
            if (superEmote != null)
                return superEmote;

            List<Emote> emotes = context.getGuild().getEmotes();
            List<Long> attempted = new ArrayList<>();

            for (Emote emote : emotes) {
                if (emote.getName().equalsIgnoreCase(humanText) && ask(context, emote, attempted))
                    return emote;
            }

            final String lowerText = humanText.toLowerCase();
            for (Emote emote : emotes) {
                if (emote.getName().toLowerCase().startsWith(lowerText) && ask(context, emote, attempted))
                    return emote;
            }

            for (Emote emote : emotes) {
                if (emote.getName().toLowerCase().contains(lowerText) && ask(context, emote, attempted))
                    return emote;
            }

            return null;
        });
    }
}
