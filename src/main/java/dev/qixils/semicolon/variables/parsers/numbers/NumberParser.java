package dev.qixils.semicolon.variables.parsers.numbers;

import dev.qixils.semicolon.Semicolon;
import dev.qixils.semicolon.variables.parsers.NonNullParser;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class NumberParser<R extends Number> extends NonNullParser<R> {
	public NumberParser(@NotNull Semicolon bot) {
		super(bot);
	}

	@Override
	public abstract @NotNull R fromDatabase(@NotNull String value) throws NumberFormatException;

	@Override
	public @NotNull String toDatabase(@NotNull R r) {
		return r.toString();
	}

	@Override
	public @NotNull CompletableFuture<@Nullable R> parseText(@NotNull Message context, @NotNull String humanText) {
		try {
			return CompletableFuture.completedFuture(fromDatabase(humanText));
		} catch (NumberFormatException exc) {
			// TODO: display error
			return CompletableFuture.completedFuture(null);
		}
	}
}
