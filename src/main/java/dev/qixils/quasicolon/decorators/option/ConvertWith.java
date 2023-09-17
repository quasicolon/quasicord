/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators.option;

import dev.qixils.quasicolon.converter.Converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes which class should be used to convert the user's input to the type
 * of a parameter annotated with {@link Option} or {@link Contextual}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConvertWith {

	/**
	 * The converter class. The class should have a public constructor which accepts one of the following:
	 * <ul>
	 *     <li>no arguments</li>
	 *     <li>one argument of type {@link dev.qixils.quasicolon.Quasicord Quasicord}</li>
	 * </ul>
	 *
	 * @return converter class
	 */
	Class<? extends Converter<?, ?>> value();
}
