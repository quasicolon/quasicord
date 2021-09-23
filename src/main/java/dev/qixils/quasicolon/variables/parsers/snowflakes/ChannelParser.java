package dev.qixils.quasicolon.variables.parsers.snowflakes;

import dev.qixils.quasicolon.QuasicolonBot;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ChannelParser<R extends GuildChannel> extends SnowflakeParser<R> {
    private final Class<R> channelClass;
    public ChannelParser(QuasicolonBot bot, Class<R> channelClass) {
        super(bot);
        this.channelClass = channelClass;
    }

    @Override
    public @Nullable R decode(@NotNull String value) {
        return (R) bot.getJDA().getGuildChannelById(value);
    }

    @Override
    public @NotNull CompletableFuture<@Nullable R> parseText(@Nullable Message context, @NotNull String humanText) {
        return super.parseText(context, humanText).thenApply(superRole -> {
            if (superRole != null || context == null)
                return superRole;

            List<GuildChannel> allChannels = context.getGuild().getChannels();
            List<R> filteredChannels = new ArrayList<>();
            for (GuildChannel chan : allChannels) {
                if (channelClass.isInstance(chan))
                    filteredChannels.add((R) chan);
            }
            List<Long> attempted = new ArrayList<>();

            for (R channel : filteredChannels) {
                if (channel.getName().equalsIgnoreCase(humanText) && ask(context, channel, attempted))
                    return channel;
            }

            final String lowerText = humanText.toLowerCase();
            for (R channel : filteredChannels) {
                if (channel.getName().toLowerCase().startsWith(lowerText) && ask(context, channel, attempted))
                    return channel;
            }

            for (R channel : filteredChannels) {
                if (channel.getName().toLowerCase().contains(lowerText) && ask(context, channel, attempted))
                    return channel;
            }

            return null;
        });
    }
}
