/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.utils;

import java.util.Optional;

public class OptionalUtil {

	/**
	 * Returns the first non-empty optional, or an empty optional if all optionals were empty.
	 *
	 * @param optionals collection of optional objects
	 * @return first non-empty optional, or an empty optional if all optionals were empty
	 */
	@SafeVarargs
	public static <T> Optional<T> or(Optional<T>... optionals) {
		for (Optional<T> optional : optionals) {
			if (optional.isPresent()) {
				return optional;
			}
		}
		return Optional.empty();
	}

	/**
	 * Returns the first non-empty optional, or an empty optional if all optionals were empty.
	 *
	 * @param optionals collection of optional objects
	 * @return first non-empty optional, or an empty optional if all optionals were empty
	 */
	public static <T> Optional<T> or(Iterable<Optional<T>> optionals) {
		for (Optional<T> optional : optionals) {
			if (optional.isPresent()) {
				return optional;
			}
		}
		return Optional.empty();
	}
}
