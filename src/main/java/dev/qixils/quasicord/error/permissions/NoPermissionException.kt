/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.error.permissions

import dev.qixils.quasicord.error.UserError
import dev.qixils.quasicord.text.LocalizableText

// TODO: clean up the unused subclasses of this
class NoPermissionException(text: LocalizableText) : UserError(text)
