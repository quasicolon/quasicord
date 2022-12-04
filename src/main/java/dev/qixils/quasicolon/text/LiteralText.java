/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.text;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

record LiteralText(@NonNull String text) implements Text {
	@Override
	public @NonNull String asString(@NonNull Locale locale) {
		return text;
	}
}
