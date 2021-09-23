package dev.qixils.quasicolon.variables.parsers.snowflakes;

import dev.qixils.quasicolon.QuasicolonBot;
import dev.qixils.quasicolon.locale.Context;
import dev.qixils.quasicolon.utils.FakeCollection;
import dev.qixils.quasicolon.utils.MessageUtil;
import dev.qixils.quasicolon.variables.parsers.VariableParser;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckReturnValue;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public abstract class SnowflakeParser<R extends ISnowflake> extends VariableParser<R> {
	public SnowflakeParser(QuasicolonBot bot) {
		super(bot);
	}

	@Override
	@NotNull
	public String encode(@NotNull R snowflake) {
		return snowflake.getId();
	}

	@Override
	public @NotNull CompletableFuture<@Nullable R> parseText(@Nullable Message context, @NotNull String humanText) {
		return CompletableFuture.supplyAsync(() -> {
			String group = SNOWFLAKE_PATTERN.matcher(humanText).group(1);
			if (group != null) {
				try {
					return decode(group);
				} catch (Exception ignored) {}
			}
			return null;
		});
	}

	private static final Collection<Long> EMPTY_COLLECTION = new FakeCollection<>();

	@CheckReturnValue
	protected boolean ask(@NotNull Message context, @Nullable IMentionable mentionable) {
		return ask(context, mentionable, EMPTY_COLLECTION);
	}

	@CheckReturnValue
	@Contract(mutates = "param3")
	protected boolean ask(@NotNull Message context, @Nullable IMentionable mentionable, @NotNull Collection<Long> attemptedObjects) {
		if (mentionable == null) return false;

		long id = mentionable.getIdLong();
		if (attemptedObjects.contains(id))
			return false;
		attemptedObjects.add(id);

		MessageChannel channel = context.getChannel();
		// TODO perm check
		CompletableFuture<@NotNull Boolean> future = new CompletableFuture<>();
		context.reply(bot.getLocaleManager().localize("snowflake_confirm", Context.fromMessage(context))) // TODO: format
				.queue(reply -> MessageUtil.setupYesNoReactionMenu(context.getAuthor().getIdLong(), reply, bool -> {
					reply.delete().queue();
					future.complete(bool != null && bool);
				}).register(bot));
		return future.join(); // TODO: this is bad :(
	}

	protected static final Pattern SNOWFLAKE_PATTERN = Pattern.compile(".*(\\d{17,19})");
}
