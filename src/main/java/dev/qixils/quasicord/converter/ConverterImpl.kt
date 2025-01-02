/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.converter

import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.internal.utils.ChannelUtil

class ConverterImpl<I, O>(
    inputClass: Class<I>,
    outputClass: Class<O>,
    private val converter: ConverterImplStep<I, O>
) : AbstractConverter<I, O>(inputClass, outputClass) {

    fun interface ConverterImplStep<I, O> {
        /**
         * Converts an input to the output type.
         *
         * @param interaction the interaction being invoked
         * @param input       the user input
         * @param targetClass the class to convert to
         * @return converted value
         */
        fun convert(interaction: Interaction, input: I, targetClass: Class<out O?>): O
    }

    constructor(
        inputClass: Class<I>,
        outputClass: Class<O>,
        converter: (Interaction, I) -> O,
    ) : this(
        inputClass,
        outputClass,
        ConverterImplStep { ctx, i, t -> converter.invoke(ctx, i) }
	)

    override fun convert(interaction: Interaction, input: I, targetClass: Class<out O?>): O {
        return converter.convert(interaction, input, targetClass)
    }

    companion object {
        fun <O : Channel?> channel(outputClass: Class<O>): Converter<Channel, O> {
            return ConverterImpl(
                Channel::class.java,
                outputClass
			) { it, channel ->
				ChannelUtil.safeChannelCast<O?>(
					channel,
					outputClass
				)
			}
		}

        fun <T> identity(clazz: Class<T>): Converter<T, T> {
            return ConverterImpl<T, T>(clazz, clazz) { it: Interaction, input -> input }
		}
    }
}
