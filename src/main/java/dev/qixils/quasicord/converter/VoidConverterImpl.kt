/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.converter

import net.dv8tion.jda.api.interactions.Interaction

// TODO: Unit ?

class VoidConverterImpl<O>(
    outputClass: Class<O>,
    private val converter: suspend (Interaction) -> O
) : AbstractConverter<Void?, O>(Void::class.java as Class<Void?>, outputClass), VoidConverter<O> {
    override suspend fun convert(interaction: Interaction): O {
        return converter.invoke(interaction)
    }
}
