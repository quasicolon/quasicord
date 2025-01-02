/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.error.syntax

import dev.qixils.quasicord.Key
import dev.qixils.quasicord.Key.Companion.library
import dev.qixils.quasicord.error.UserError
import dev.qixils.quasicord.text.Text
import dev.qixils.quasicord.text.Text.Companion.single

// TODO: easy constructor for JDA args? idk what this would involve tbh
class InvalidSyntaxException(argumentKey: Key, subError: Text) : UserError(
    single(
        library("exception.invalid_syntax"),
        single(argumentKey),
        subError
    )
) {
    companion object {
        private val UNKNOWN_ARGUMENT = library("arg._unknown_")
    }
}
