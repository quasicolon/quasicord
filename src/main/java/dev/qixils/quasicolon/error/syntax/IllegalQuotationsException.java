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

public class IllegalQuotationsException extends UserError {
	private static final @NonNull Text ERROR_TEXT = Text.single(Key.library("exception.invalid_syntax.quotations"));

	// same TODO as parent class

	public IllegalQuotationsException(@NonNull Key argumentKey) {
		super(argumentKey, ERROR_TEXT);
	}
}
