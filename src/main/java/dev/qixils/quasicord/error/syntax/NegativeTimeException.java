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

public class NegativeTimeException extends UserError {
	private static final @NonNull Text ERROR_TEXT = Text.single(Key.library("exception.negative_time"));

	// same TODO as parent class

	public NegativeTimeException(@NonNull Key argumentKey) {
		super(argumentKey, ERROR_TEXT);
	}
}
