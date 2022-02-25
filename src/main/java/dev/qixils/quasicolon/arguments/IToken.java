package dev.qixils.quasicolon.arguments;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;
import java.util.Objects;

/**
 * A class that contains strings which input may be tested against.
 */
public interface IToken {

	/**
	 * Tests if a given input matches this token.
	 * @param input a lowercase input string
	 * @return whether the input matches the token
	 */
	boolean matches(@NonNull String input);

	/**
	 * Converts the input to lowercase then tests if it matches this token.
	 * @param input a string
	 * @return whether the input matches the token
	 */
	default boolean toLowerMatches(@NonNull String input) {
		return matches(Objects.requireNonNull(input, "input cannot be null").toLowerCase(Locale.ENGLISH));
	}
}
