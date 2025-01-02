/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators

import dev.qixils.quasicord.converter.Converter
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
@JvmRecord
data class ConverterData(val converter: Converter<*, *>, val optName: String?, val targetClass: Class<*>)
