package dev.qixils.quasicolon.variables.parsers.numbers;

import dev.qixils.quasicolon.QuasicolonBot;
import dev.qixils.quasicolon.variables.parsers.NonNullParser;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public abstract class NumberParser<R extends Number> extends NonNullParser<R> {
	private final ParserFilter filter;

	public NumberParser(@NotNull QuasicolonBot bot, @Nullable ParserFilter filter) {
		super(bot);
		this.filter = filter == null ? ParserFilter.ALL_NUMBERS : filter;
	}

	@Override
	public abstract @NotNull R fromDatabase(@NotNull String value) throws NumberFormatException;

	@Override
	public @NotNull String toDatabase(@NotNull R r) {
		return r.toString();
	}

	@Override
	public @NotNull CompletableFuture<@Nullable R> parseText(@Nullable Message context, @NotNull String humanText) {
		try {
			R value = fromDatabase(humanText);
			if (!filter.test(value))
				value = null; // TODO: display error
			return CompletableFuture.completedFuture(value);
		} catch (NumberFormatException exc) {
			// TODO: display error
			return CompletableFuture.completedFuture(null);
		}
	}

	/**
	 * Determines what types of numbers users are allowed to input for a given variable.
	 * Does not affect the output of {@link #fromDatabase(String)}.
	 */
	public enum ParserFilter implements Predicate<Number> {
		/**
		 * Allows users to input any number.
		 */
		ALL_NUMBERS {
			@Override
			public boolean test(Number number) {
				return true;
			}
		},
		/**
		 * Allows users to input any non-negative number (i.e. any value greater than or equal to zero).
		 */
		NON_NEGATIVE {
			@Override
			public boolean test(Number number) {
				return number.doubleValue() >= 0;
			}
		},
		/**
		 * Allows users to input any positive number (i.e. any value greater than zero).
		 */
		ONLY_POSITIVE {
			@Override
			public boolean test(Number number) {
				return number.doubleValue() > 0;
			}
		};
	}
}
