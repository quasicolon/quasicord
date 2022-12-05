/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators.option;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes an array of {@link Choice}s which should be suggested to the user.
 * Unlike {@link Choices} (which is annotation is mutually exclusive with), users are free to ignore these suggestions.
 *
 * @see Choices @Choices for a hardcoded list of choices
 * @see AutoCompleteWith @AutoCompleteWith for dynamically generating suggestions
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoCompleteFrom {

	/**
	 * The array of choices to suggest to the user.
	 *
	 * @return array of choices
	 */
	Choice[] value();
}
