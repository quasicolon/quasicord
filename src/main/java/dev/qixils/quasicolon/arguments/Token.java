/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.arguments;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A piece of text which may either have only a singular form or both a singular and plural form.
 */
final class Token implements IToken {
	private final @NonNull String singular;
	private final @Nullable String plural;

	private Token(@NonNull String singular, @Nullable String plural) {
		this.singular = singular;
		this.plural = plural;
	}

	/**
	 * Creates a token with only a singular form.
	 * @param singular singular form
	 * @return new token
	 */
	public static Token ofSingular(@NonNull String singular) {
		return new Token(singular, null);
	}

	/**
	 * Creates a token with a singular form and an automatically generated plural form.
	 * @param singular singular form
	 * @return new token
	 */
	public static Token ofPlural(@NonNull String singular) {
		return new Token(singular, singular + "s");
	}

	/**
	 * Creates a token with a singular form and a plural form.
	 * @param singular singular form
	 * @param plural plural form
	 * @return new token
	 */
	public static Token ofPlural(@NonNull String singular, @NonNull String plural) {
		return new Token(singular, plural);
	}

	/**
	 * Tests if a given input matches this token.
	 * @param input a lowercase input string
	 * @return whether the input matches the token
	 */
	public boolean matches(@NonNull String input) {
		return singular.equals(input) || (plural != null && plural.equals(input));
	}
}
