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

public class InvalidSyntaxException extends UserError {
	private static final @NonNull Key UNKNOWN_ARGUMENT = Key.library("arg._unknown_");

	// TODO: easy constructor for JDA args? idk what this would involve tbh

	public InvalidSyntaxException(@NonNull Key argumentKey, @NonNull Text subError) {
		super(Text.single(
				Key.library("exception.invalid_syntax"),
				Text.single(argumentKey),
				subError
		));
	}
}
