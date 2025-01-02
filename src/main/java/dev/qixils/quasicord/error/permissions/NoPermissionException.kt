/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.error.permissions;

import dev.qixils.quasicord.error.UserError;
import dev.qixils.quasicord.text.LocalizableText;
import org.checkerframework.checker.nullness.qual.NonNull;

// TODO: clean up the unused subclasses of this

public class NoPermissionException extends UserError {
	public NoPermissionException(@NonNull LocalizableText text) {
		super(text);
	}
}
