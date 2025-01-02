/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.events;

import dev.qixils.quasicord.registry.Registry;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is listening for the initialization of a specific registry.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RegistryInitListener {
	/**
	 * The type of registry to listen to.
	 *
	 * @return registry type
	 */
	@NonNull Class<? extends Registry<?>> value();
}
