package dev.qixils.quasicolon.variables.parsers;

import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.variables.Variable;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NonBlocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import javax.annotation.CheckReturnValue;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A type of variable that may be set via {@code ;config}.
 *
 * @param <R> the output class
 */
public abstract class VariableParser<R> {
	protected final @NotNull Quasicolon bot;

	public VariableParser(final @NotNull Quasicolon bot) {
		this.bot = Objects.requireNonNull(bot, "bot cannot be null");
	}

	/**
	 * Fetches a value from the database and converts it to the resulting class.
	 * <p>
	 * The value that emits from the {@link Mono} may be {@code null}.
	 *
	 * @param guild    guild to fetch the variable from
	 * @param variable variable to fetch
	 * @return converted object, or null if unavailable
	 */
	@NotNull
	@CheckReturnValue
	@NonBlocking
	public Mono<@Nullable R> fromDatabase(long guild, @NotNull String variable) {
		return bot.getDatabaseManager()
				.getAllByEquals(Map.of("guildId", guild, "name", variable), Variable.class)
				.next().mapNotNull(var -> decode(var.getData()));
	}

	/**
	 * Converts a value from the database to the resulting class.
	 *
	 * @param value database value
	 * @return converted object, or null if unavailable
	 */
	@Nullable
	@CheckReturnValue
	@NonBlocking
	public abstract R decode(@NotNull String value);

	/**
	 * Converts a class to database value.
	 *
	 * @param r converted object
	 * @return database value
	 */
	@NotNull
	@CheckReturnValue
	@NonBlocking
	public abstract String encode(@NotNull R r);

	/**
	 * Parses human text into a converted object.
	 *
	 * @param context   the message invoking this method
	 * @param humanText human-writable text
	 * @return converted object
	 */
	@NotNull
	@CheckReturnValue
	@NonBlocking
	public abstract CompletableFuture<@Nullable R> parseText(@Nullable Message context, @NotNull String humanText);

	/**
	 * Parses human text into a converted object.
	 *
	 * @param context   the message invoking this method
	 * @param humanText human-writable text
	 * @return converted object
	 */
	@NotNull
	@CheckReturnValue
	@NonBlocking
	public Mono<@Nullable R> parseTextAsMono(@Nullable Message context, @NotNull String humanText) {
		return Mono.fromFuture(parseText(context, humanText));
	}
}
