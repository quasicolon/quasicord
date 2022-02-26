package dev.qixils.quasicolon.variables.parsers.snowflakes;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.QuasicolonBot;
import dev.qixils.quasicolon.text.Text;
import dev.qixils.quasicolon.utils.FakeCollection;
import dev.qixils.quasicolon.utils.MessageUtil;
import dev.qixils.quasicolon.variables.parsers.VariableParser;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

import javax.annotation.CheckReturnValue;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public abstract class SnowflakeParser<R extends ISnowflake> extends VariableParser<R> {
	protected static final @NonNull Pattern SNOWFLAKE_PATTERN = Pattern.compile(".*(\\d{17,19})");
	private static final @NonNull Collection<Long> EMPTY_COLLECTION = new FakeCollection<>();

	public SnowflakeParser(@NonNull QuasicolonBot bot) {
		super(bot);
	}

	@Override
	public @NonNull String encode(@NonNull R snowflake) {
		return snowflake.getId();
	}

	@Override
	public @NonNull CompletableFuture<@Nullable R> parseText(@Nullable Message context, @NonNull String humanText) {
		return CompletableFuture.supplyAsync(() -> {
			String group = SNOWFLAKE_PATTERN.matcher(humanText).group(1);
			if (group != null) {
				try {
					return decode(group);
				} catch (Exception ignored) {
				}
			}
			return null;
		});
	}

	@CheckReturnValue
	protected boolean ask(@NonNull Message context, @Nullable IMentionable mentionable) {
		return ask(context, mentionable, EMPTY_COLLECTION);
	}

	@CheckReturnValue
	@Contract(mutates = "param3")
	protected boolean ask(@NonNull Message context, @Nullable IMentionable mentionable, @NonNull Collection<Long> attemptedObjects) {
		if (mentionable == null) return false;

		long id = mentionable.getIdLong();
		if (attemptedObjects.contains(id))
			return false;
		attemptedObjects.add(id);

		MessageChannel channel = context.getChannel();
		// TODO perm check
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		Text.single(Key.library("snowflake_confirm"))
				.sendAsReplyTo(context)
				.queue(reply -> MessageUtil.setupYesNoReactionMenu(context.getAuthor().getIdLong(), reply, input -> {
					reply.delete().queue();
					future.complete(input != null && input);
				}).register(bot));
		return future.join(); // TODO: this is bad :(
	}
}
