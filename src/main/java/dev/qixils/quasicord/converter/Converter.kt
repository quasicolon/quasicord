/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.converter;

import net.dv8tion.jda.api.interactions.Interaction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus;

/**
 * An interface for converting a user-provided value to a different type.
 *
 * @param <I> the input type
 * @param <O> the output type
 */
public interface Converter<I, O> {

	/**
	 * Returns the class of the input type.
	 *
	 * @return input class
	 */
	@NonNull
	Class<I> getInputClass();

	/**
	 * Returns the class of the output type.
	 *
	 * @return output class
	 */
	@NonNull
	Class<O> getOutputClass();

	/**
	 * Converts an input to the output type.
	 *
	 * @param interaction the interaction being invoked
	 * @param input       the user input
	 * @param targetClass the class to convert to
	 * @return converted value
	 */
	@NonNull
	O convert(@NonNull Interaction interaction, @NonNull I input, @NonNull Class<? extends O> targetClass);

	/**
	 * Converts an input to the output type.
	 *
	 * @param interaction the interaction being invoked
	 * @param input       the user input
	 * @return converted value
	 */
	@NonNull
	@ApiStatus.NonExtendable
	default O convert(@NonNull Interaction interaction, @NonNull I input) {
		return convert(interaction, input, getOutputClass());
	}

	/**
	 * Determines whether this converter can be converted to in a converter chain.
	 *
	 * @return true if this converter can be converted to
	 */
	default boolean canConvertTo() {
		return true;
	}

	/**
	 * Determines whether this converter can be converted from in a converter chain.
	 *
	 * @return true if this converter can be converted from
	 */
	default boolean canConvertFrom() {
		return true;
	}
}
