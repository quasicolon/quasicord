/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.error.syntax;

import dev.qixils.quasicord.Key;
import dev.qixils.quasicord.error.UserError;
import dev.qixils.quasicord.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UnknownFormatException extends UserError {

	// same TODO as parent class

	public UnknownFormatException(@NonNull Key argumentKey, @NonNull String input) {
		super(argumentKey, getErrorText(input));
	}

	private static @NonNull Text getErrorText(@NonNull String token) {
		return Text.single(Key.library("exception.unknown_format"), token);
	}
}
