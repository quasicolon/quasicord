/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.registry.core;

import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.registry.impl.ClosableMappedRegistryImpl;
import dev.qixils.quasicolon.variables.parsers.PrefixParser;
import dev.qixils.quasicolon.variables.parsers.VariableParser;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The registry of {@link VariableParser variables}.
 */
public final class VariableRegistry extends ClosableMappedRegistryImpl<VariableParser<?>> {

	public final PrefixParser PREFIX;

	VariableRegistry(@NonNull Quasicolon quasicolon) {
		super("variables", true);
		PREFIX = typedRegister("prefix", new PrefixParser(quasicolon));
	}

	private <R extends VariableParser<?>> R typedRegister(@NonNull String key, @NonNull R value) {
		register(key, value);
		return value;
	}
}
