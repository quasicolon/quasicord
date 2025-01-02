/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class QuasicordConfig(val token: String, environment: Environment?) {
    val environment: Environment = environment ?: Environment.TEST
}
