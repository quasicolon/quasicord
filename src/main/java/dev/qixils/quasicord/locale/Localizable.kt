/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale

import dev.qixils.quasicord.Key

/**
 * An object which possesses a translation key.
 */
interface Localizable {

    /**
     * Translation key corresponding to this object.
     *
     * @return translation key
     */
	val key: Key
}
