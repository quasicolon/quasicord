/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.locale.impl;

import dev.qixils.quasicolon.locale.Context;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
public record ImmutableContextImpl(long user, long channel, long guild) implements Context {
	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public @NotNull Context user(long user) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull Context channel(long channel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull Context guild(long guild) {
		throw new UnsupportedOperationException();
	}
}

