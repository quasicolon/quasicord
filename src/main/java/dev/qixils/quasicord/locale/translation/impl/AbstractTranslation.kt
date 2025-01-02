/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale.translation.impl

import dev.qixils.quasicord.locale.translation.Translation
import java.util.*

/**
 * Abstract implementation of [Translation].
 */
abstract class AbstractTranslation protected constructor(
	override val key: String,
	override val locale: Locale,
	override val requestedLocale: Locale
) : Translation
