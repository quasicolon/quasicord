/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.registry.impl

import dev.qixils.quasicord.registry.Registry

abstract class AbstractRegistryImpl<T> protected constructor(override val id: String) : Registry<T>
