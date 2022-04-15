/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.error.permissions;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.text.Text;

public class DMOnlyException extends NoPermissionException {
	public DMOnlyException() {
		super(Text.single(Key.library("exception.dms_only")));
	}
}
