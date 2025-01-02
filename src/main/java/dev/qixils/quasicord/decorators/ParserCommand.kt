/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators

import dev.qixils.quasicord.cogs.impl.AbstractCommand
import dev.qixils.quasicord.converter.Converter
import dev.qixils.quasicord.locale.Context
import dev.qixils.quasicord.text.Text
import dev.qixils.quasicord.utils.QuasiMessage
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.CommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

internal abstract class ParserCommand<I : GenericCommandInteractionEvent> protected constructor(
    protected val parser: AnnotationParser,
    commandData: CommandData?,
    interactionClass: Class<I>,
    guildId: String?
) : AbstractCommand<I>(commandData, interactionClass, guildId) {
    protected fun createConverter(converterClass: Class<out Converter<*, *>?>): Converter<*, *> {
        val commandManager = parser.commandManager
        for (constructor in converterClass.getConstructors()) {
            if (constructor.parameterCount == 0) {
                try {
                    return constructor.newInstance() as Converter<*, *>
                } catch (e: Exception) {
                    throw IllegalArgumentException("Failed to construct converter", e)
                }
            } else if (constructor.parameterCount == 1) {
                val argClass = constructor.parameterTypes[0]
				// get arg to construct with
				val arg = if (argClass.isAssignableFrom(commandManager.library.javaClass)) commandManager.library
				else if (argClass.isAssignableFrom(commandManager.javaClass)) commandManager
				else throw IllegalArgumentException("Converter constructor must take Quasicord as an argument")
                // construct
                try {
                    return constructor.newInstance(arg) as Converter<*, *>
                } catch (e: Exception) {
                    throw IllegalArgumentException("Failed to construct converter", e)
                }
            } else {
                throw IllegalArgumentException("Converter constructor must have 0 or 1 parameters; see @") // TODO: ?
            }
        }
        throw IllegalArgumentException("Converter must have a no-arg or Quasicord constructor")
    }

    companion object {
        @JvmStatic
		fun consumeCommandResult(interaction: CommandInteraction, result: Any?) {
            when (result) {
				is Mono<*> -> result.subscribe { res: Any? -> consumeCommandResult(interaction, res!!) }
                is CompletableFuture<*> -> result.thenAccept { res: Any? -> consumeCommandResult(interaction, res!!) }
                is Boolean -> interaction.deferReply(result).queue()
                is QuasiMessage -> result.text.asString(Context.fromInteraction(interaction)).subscribe(Consumer { string: String? ->
                    val action = interaction.reply(string!!)
					result.modifier.accept(action)
                    action.queue()
                })
                is Text -> result.asString(Context.fromInteraction(interaction)).subscribe(Consumer { msg: String? ->
                    interaction.reply(
                        msg!!
                    ).queue()
                })
                is String -> interaction.reply(result).queue()
				null -> {}
                else -> throw IllegalArgumentException("Unsupported response type: " + result.javaClass.name)
            }
        }
    }
}
