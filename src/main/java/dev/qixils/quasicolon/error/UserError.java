/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.error;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.text.LocalizableText;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UserError extends LocalizedRuntimeException {

	public UserError(@NonNull LocalizableText text) {
		super(text);
	}

	public UserError(@NonNull Key key, Object @NonNull ... args) {
		super(key, args);
	}
}
