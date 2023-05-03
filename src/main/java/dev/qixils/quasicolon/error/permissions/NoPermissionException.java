/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.error.permissions;

import dev.qixils.quasicolon.error.UserError;
import dev.qixils.quasicolon.text.LocalizableText;
import org.checkerframework.checker.nullness.qual.NonNull;

// TODO: clean up the unused subclasses of this

public class NoPermissionException extends UserError {
	public NoPermissionException(@NonNull LocalizableText text) {
		super(text);
	}
}
