/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators.option

import net.dv8tion.jda.api.entities.channel.ChannelType

/**
 * Denotes which [ChannelType]s are supported by an [Option] of
 * [type][Option.type] [CHANNEL][net.dv8tion.jda.api.interactions.commands.OptionType.CHANNEL].
 *
 * @see Option
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ChannelTypes(

    /**
     * The supported channel types.
     *
     * @return supported channel types
     */
    vararg val value: ChannelType
)
