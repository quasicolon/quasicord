package dev.qixils.semicolon.variables.parsers;

import com.mongodb.client.model.Filters;
import dev.qixils.semicolon.Semicolon;
import dev.qixils.semicolon.variables.Variable;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

/**
 * A type of variable that may be set via {@code ;config}.
 * @param <R> the output class
 */
@AllArgsConstructor
public abstract class VariableParser<R> {
	protected final @NotNull Semicolon bot;

	/**
	 * Fetches a value from the database and converts it to the resulting class.
	 * <p>
	 * The value that emits from the {@link Mono} may be {@code null}.
	 *
	 * @param guild guild to fetch the variable from
	 * @param variable variable to fetch
	 * @return converted object, or null if unavailable
	 */
	@NotNull
	public Mono<R> fromDatabase(long guild, String variable) {
		return bot.getDatabase().getAllBy(Filters.and(Filters.eq("guildId", guild), Filters.eq("name", variable)), Variable.class).next().map(var -> {
			if (var == null)
				return null;
			return fromDatabase(var.getData());
		});
	}

	/**
	 * Converts a value from the database to the resulting class.
	 *
	 * @param value database value
	 * @return converted object, or null if unavailable
	 */
	@Nullable
	public abstract R fromDatabase(@NotNull String value);

	/**
	 * Converts a class to database value.
	 * @param r converted object
	 * @return database value
	 */
	@NotNull
	public abstract String toDatabase(R r);

	/**
	 * Parses human text into a converted object.
	 *
	 * @param context the message invoking this method
	 * @param humanText human-writable text
	 * @return converted object
	 */
	@NotNull
	public abstract CompletableFuture<R> parseText(@NotNull Message context, @NotNull String humanText);

	/**
	 * Parses human text into a converted object.
	 *
	 * @param context the message invoking this method
	 * @param humanText human-writable text
	 * @return converted object
	 */
	@NotNull
	public Mono<R> parseTextAsMono(@NotNull Message context, @NotNull String humanText) {
		return Mono.fromFuture(parseText(context, humanText));
	}
}
