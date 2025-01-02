/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.converter

import net.dv8tion.jda.api.interactions.Interaction

/**
 * An interface for converting an interaction to a different type.
 */
interface VoidConverter<O> : Converter<Void?, O> {
    override suspend fun convert(interaction: Interaction, input: Void?, targetClass: Class<out O?>): O {
        return convert(interaction)
    }

    /**
     * Converts an interaction to the output type.
     *
     * @param interaction the interaction being invoked
     * @return converted value
     */
    suspend fun convert(interaction: Interaction): O
}
