/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.events

import dev.qixils.quasicord.registry.Registry
import kotlin.reflect.KClass

/**
 * Indicates that a method is listening for the initialization of a specific registry.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class RegistryInitListener(

    /**
     * The type of registry to listen to.
     *
     * @return registry type
     */
    val value: KClass<out Registry<*>>
)
