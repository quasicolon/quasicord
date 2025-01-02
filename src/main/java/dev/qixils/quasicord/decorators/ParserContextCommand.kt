/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators

import dev.qixils.quasicord.converter.Converter
import dev.qixils.quasicord.converter.VoidConverterImpl
import dev.qixils.quasicord.decorators.option.ConvertWith
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.lang.reflect.Method

internal class ParserContextCommand(
    private val id: String,
    parser: AnnotationParser,
    command: CommandData,
    `object`: Any,
    method: Method,
    guildId: String?
) : ParserCommand<GenericContextInteractionEvent<*>>(
    parser,
    command,
    GenericContextInteractionEvent::class.java,
    guildId
) {
    private val converters: Array<Converter<*, *>?>
    private val `object`: Any
    private val method: Method

    init {
        val commandManager = parser.commandManager
        this.`object` = `object`
        this.method = method

        // parameters
        converters = arrayOfNulls<Converter<*, *>>(method.parameterCount)
        for (i in 0..<method.parameterCount) {
            // set converter
            val parameter = method.parameters[i]
            val convertWith = parameter.getAnnotation<ConvertWith?>(ConvertWith::class.java)

            // converter data
            val converter: Converter<*, *>?

            if (convertWith != null) {
                converter = createConverter(convertWith.value.java)
            } else {
                if (Interaction::class.java.isAssignableFrom(parameter.getType())) converter =
                    VoidConverterImpl(Interaction::class.java) { it }
                else {
                    converter = commandManager.library.rootRegistry.converterRegistry.findConverter(
                        Void::class.java,
                        parameter.getType()
                    )
                    requireNotNull(converter) {
                        "No converter found for parameter " + parameter.name + " of type " + parameter.getType()
                            .getName()
                    }
                }
            }

            converters[i] = converter
        }
    }

	override val commandData: CommandData
		get() = super.commandData!!

	override val name: String
		get() = id

	override val discordName: String
		get() = commandData.name

    override fun accept(interaction: GenericContextInteractionEvent<*>) {
        // note: this has no try/catch because that is being handled at an even higher level than this

        // fetch args

        val args = arrayOfNulls<Any>(converters.size)
        for (i in args.indices) {
			val converter = converters[i]!! as Converter<Any?, Any?>
            args[i] = converter.convert(interaction, null)
        }

        // invoke and handle
        try {
            consumeCommandResult(interaction, method.invoke(`object`, *args))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
