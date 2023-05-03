/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.error.syntax;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.error.UserError;
import dev.qixils.quasicolon.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UnknownTokenException extends UserError {

	// same TODO as parent class

	public UnknownTokenException(@NonNull Key argumentKey, @NonNull String token) {
		super(argumentKey, getErrorText(token));
	}

	private static @NonNull Text getErrorText(@NonNull String token) {
		return Text.single(Key.library("exception.unknown_token"), token);
	}
}
