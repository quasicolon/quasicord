package dev.qixils.semicolon.variables.parsers.snowflakes;

import dev.qixils.semicolon.Localizer.Context;
import dev.qixils.semicolon.Semicolon;
import dev.qixils.semicolon.utils.FakeCollection;
import dev.qixils.semicolon.variables.parsers.VariableParser;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public abstract class SnowflakeParser<R extends ISnowflake> extends VariableParser<R> {
	public SnowflakeParser(Semicolon bot) {
		super(bot);
	}

	@Override
	@NotNull
	public String toDatabase(R snowflake) {
		return snowflake.getId();
	}

	@Override
	public @NotNull CompletableFuture<R> parseText(@NotNull Message context, @NotNull String humanText) {
		CompletableFuture<R> future = new CompletableFuture<>();
		String group = SNOWFLAKE_PATTERN.matcher(humanText).group(1);
		if (group != null) {
			try {
				R result = fromDatabase(group);
				if (result != null)
					future.complete(result);
			} catch (Exception ignored) {}
		}
		return future;
	}

	private static final Collection<Long> EMPTY_COLLECTION = new FakeCollection<>();

	protected boolean ask(@NotNull Message context, @Nullable IMentionable mentionable) {
		return ask(context, mentionable, EMPTY_COLLECTION);
	}

	@Contract(mutates = "param3")
	protected boolean ask(@NotNull Message context, @Nullable IMentionable mentionable, @NotNull Collection<Long> attemptedUsers) {
		if (mentionable == null) return false;

		long id = mentionable.getIdLong();
		if (attemptedUsers.contains(id))
			return false;
		attemptedUsers.add(id);

		context.reply(bot.getLocalizer().localize("snowflake_confirm", Context.fromMessage(context))) // TODO: format
				.queue(message -> {
					message.addReaction().queue();
					message.addReaction().queue();
				});
	}

	protected static final Pattern SNOWFLAKE_PATTERN = Pattern.compile(".*(\\d{17,19})");
}
