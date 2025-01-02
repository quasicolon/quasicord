/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators.option

import dev.qixils.quasicord.converter.Converter
import kotlin.reflect.KClass

/**
 * Denotes which class should be used to convert the user's input to the type
 * of a parameter annotated with [Option] or [Contextual].
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConvertWith(
    /**
     * The converter class. The class should have a public constructor which accepts one of the following:
     *
     *  * no arguments
     *  * one argument of type [Quasicord][dev.qixils.quasicord.Quasicord]
     *
     * @return converter class
     */
    val value: KClass<out Converter<*, *>>
)
