/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.registry.core;

import dev.qixils.quasicord.registry.impl.MappedRegistryImpl;
import dev.qixils.quasicord.variables.parsers.VariableParser;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The registry of {@link VariableParser variables}.
 */
public final class VariableRegistry extends MappedRegistryImpl<VariableParser<?>> {

	VariableRegistry() {
		super("variables");
	}

	public <R extends VariableParser<?>> R typedRegister(@NonNull String key, @NonNull R value) {
		register(key, value);
		return value;
	}
}
