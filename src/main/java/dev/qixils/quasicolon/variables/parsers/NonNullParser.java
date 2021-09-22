package dev.qixils.quasicolon.variables.parsers;

import dev.qixils.quasicolon.QuasicolonBot;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * A variable parser whose parsed values should never be null, i.e. the values should always be available.
 * This is used for primitive objects such as integers and strings.
 * @param <R>
 */
public abstract class NonNullParser<R> extends VariableParser<R> {
	public NonNullParser(@NotNull QuasicolonBot bot) {
		super(bot);
	}

	@Override
	public final @NotNull Mono<@NotNull R> fromDatabase(long guild, @NotNull String variable) {
		return super.fromDatabase(guild, variable);
	}

	@Override
	public abstract @NotNull R fromDatabase(@NotNull String value);
}
