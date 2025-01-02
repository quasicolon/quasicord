/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators

import dev.qixils.quasicord.autocomplete.AutoCompleter
import dev.qixils.quasicord.autocomplete.AutoCompleterFrom
import dev.qixils.quasicord.cogs.SlashCommand
import dev.qixils.quasicord.cogs.SlashCommandDataBranch
import dev.qixils.quasicord.converter.Converter
import dev.qixils.quasicord.converter.VoidConverter
import dev.qixils.quasicord.converter.VoidConverterImpl
import dev.qixils.quasicord.decorators.option.*
import dev.qixils.quasicord.locale.TranslationProvider
import dev.qixils.quasicord.locale.translation.UnknownTranslation
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.lang.reflect.Method
import java.util.*

internal class ParserSlashCommand(
	override val name: String,
	parser: AnnotationParser,
	i18n: TranslationProvider,
	branch: SlashCommandDataBranch,
	`object`: Any?,
	method: Method,
	guildId: String?
) : ParserCommand<SlashCommandInteractionEvent>(
    parser,
    branch.rootIfStandalone,
    SlashCommandInteractionEvent::class.java,
    guildId
), SlashCommand {
    private val i18n: TranslationProvider
    override val branch: SlashCommandDataBranch
    private val converters: Array<ConverterData>
    private val obj: Any?
    private val method: Method

    init {
        val commandManager = parser.commandManager
        this.i18n = i18n
        val namespace = i18n.namespace
        this.branch = branch
        this.obj = `object`
        this.method = method

        // parameters
        val tempConverters = arrayOfNulls<ConverterData>(method.parameterCount)
        for (i in 0 ..< method.parameterCount) {
            // set converter
            val parameter = method.parameters[i]
            val contextual = parameter.getAnnotation(Contextual::class.java)
            val option = parameter.getAnnotation(Option::class.java)
            require(!(contextual != null && option != null)) { "Cannot have both @Contextual and @Option on the same parameter" }
            val convertWith = parameter.getAnnotation(ConvertWith::class.java)

            // converter data
            val converter: Converter<*, *>?
            var optNameStr: String? = null

            if (convertWith != null) {
                converter = createConverter(convertWith.value.java)
            } else if (contextual != null) {
                if (Interaction::class.java.isAssignableFrom(parameter.getType()))
					converter = VoidConverterImpl(Interaction::class.java) { it }
				else {
                    converter = commandManager.library.rootRegistry.converterRegistry.findConverter(
                        Void::class.java,
                        parameter.getType()
                    )
                    requireNotNull(converter) {
                        "No converter found for parameter " + parameter.name + " of type " + parameter.getType().getName()
                    }
                }
            } else if (option != null) {
                val inputClass = parseInputClass(parameter.getType(), option.type)
                converter =
                    commandManager.library.rootRegistry.converterRegistry.findConverter(inputClass, parameter.getType())
                requireNotNull(converter) {
                    "No converter found for parameter " + parameter.name + " of type " + parameter.getType().getName()
                }
            } else {
                throw IllegalArgumentException("Parameters must be annotated with @Contextual or @Option")
            }

            if (option != null) {
                // register option
                val optId: String? = option.value
                val fullOptId = "$name.options.$optId"
                val acWith = parameter.getAnnotation(AutoCompleteWith::class.java)
                val acFrom = parameter.getAnnotation(AutoCompleteFrom::class.java)
                val range = parameter.getAnnotation(Range::class.java)
                val channelTypes = parameter.getAnnotation(ChannelTypes::class.java)
                val choices = parameter.getAnnotationsByType(Choice::class.java)

                // name
                val optName = i18n.getSingle("$name.options.$optId.name", i18n.defaultLocale)
                check(optName !is UnknownTranslation) { "Missing translation for option $namespace:$name.options.$optId.name" }
                optNameStr = optName.get()

                // description
                val optDescription = i18n.getSingle("$name.options.$optId.description", i18n.defaultLocale)
                check(optDescription !is UnknownTranslation) { "Missing translation for option $namespace:$name.options.$optId.description" }
                val optDescriptionStr = optDescription.get()

                // option
                val opt = OptionData(
                    option.type,
                    optNameStr,
                    optDescriptionStr,
                    option.required,
                    acWith != null || acFrom != null
                )
                opt.setNameLocalizations(i18n.getDiscordTranslations("$name.options.$optId.name"))
                opt.setDescriptionLocalizations(i18n.getDiscordTranslations("$name.options.$optId.description"))

                // range
                if (range != null) {
                    if (option.type == OptionType.INTEGER) opt.setRequiredRange(range.min.toLong(), range.max.toLong())
                    else if (option.type == OptionType.NUMBER) opt.setRequiredRange(range.min, range.max)
                    else if (option.type == OptionType.STRING) opt.setRequiredLength(
                        range.min.toInt(),
                        range.max.toInt()
                    )
                    else throw IllegalArgumentException("Cannot use @Range on option of type " + option.type)
                }

                // channel types
                if (channelTypes != null) opt.setChannelTypes(*channelTypes.value)

                // choices
                if (choices.size > 0) {
                    require(opt.type.canSupportChoices()) { "Cannot use @Choice on option of type " + option.type }
                    opt.addChoices(*createChoices(choices, option.type, "$name.options.$optId.choices."))
                }

                // auto complete
                require(!(acWith != null && acFrom != null)) { "Cannot have both @AutoCompleteWith and @AutoCompleteFrom on the same command" }
                if (acWith != null || acFrom != null) {
                    require(option.type.canSupportChoices()) { "Cannot use @Choice on option of type " + option.type }
                }
                if (acWith != null) {
                    val autoCompleter = parser.registerAutoCompleter(acWith.value.java)
                    parser.putAutoCompleter(fullOptId, autoCompleter)
                } else if (acFrom != null) {
                    val autocompletes = createChoices(acFrom.value, option.type, "$fullOptId.choices.")
                    val autoCompleter: AutoCompleter = AutoCompleterFrom(*autocompletes)
                    parser.putAutoCompleter(fullOptId, autoCompleter)
                }

                val subcommand = branch.subcommand
                if (subcommand != null) subcommand.addOptions(opt)
                else branch.root.addOptions(opt)
            }

			tempConverters[i] = ConverterData(converter, optNameStr, parameter.getType())
        }
		converters = tempConverters.filterNotNull().toTypedArray()
    }

    override val discordName: String
        get() = branch.name

    override fun accept(interaction: SlashCommandInteractionEvent) {
        // note: this has no try/catch because that is being handled at an even higher level than this

        // fetch args

        val args = arrayOfNulls<Any>(converters.size)
        for (i in args.indices) {
            val converterData = converters[i]
            val converter = converterData.converter
            if (converter is VoidConverter<*>) {
                args[i] = converter.convert(interaction)
                continue
            }
			converter as Converter<Any, Any> // smart cast to generics to allow unchecked input
            val optName = Objects.requireNonNull<String?>(
                converterData.optName,
                "optName should only be null for @Contextual parameters"
            )
            val option = interaction.getOption(optName)
            if (option == null) {
                args[i] = null
                continue
            }

            val inputClass: Class<*> = converter.inputClass
			// TODO: Maybe this would be better done with a pair of hashmaps between Class<?> and Converter<?, ?>?
			//  and a method on the converter that converts from just the Interaction and OptionMapping.
			// Extract the appropriate type from the interaction option for the converter:
			val input: Any = if (inputClass == String::class.java) option.asString
			else if (inputClass == Long::class.java) option.asInt
			else if (inputClass == Double::class.java) option.asDouble
			else if (inputClass == Boolean::class.java) option.asBoolean
			else if (inputClass == Channel::class.java) option.asChannel
			else if (inputClass == Role::class.java) option.asRole
			else if (inputClass == IMentionable::class.java) option.asMentionable
			else if (inputClass == Attachment::class.java) option.asAttachment
			else if (inputClass == User::class.java) option.asUser
			else if (inputClass == Member::class.java) option.asMember ?: error("Member was not found in this guild for ${option.name}")
			else error("Could not accept interaction option of type ${option.type} for a converter from ${inputClass.name}") // extract the appropriate type from the interaction option for the converter:
			args[i] = converter.convert(interaction, input, converterData.targetClass)
        }

        // invoke and handle
        try {
            consumeCommandResult(interaction, method.invoke(obj, *args))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun parseInputClass(outputClass: Class<*>, optionType: OptionType): Class<*> {
        return when (optionType) {
            OptionType.STRING -> String::class.java
            OptionType.INTEGER -> Long::class.java
            OptionType.BOOLEAN -> Boolean::class.java
            OptionType.USER -> if (outputClass == Member::class.java) Member::class.java else User::class.java
            OptionType.CHANNEL -> Channel::class.java
            OptionType.ROLE -> Role::class.java
            OptionType.MENTIONABLE -> IMentionable::class.java
            OptionType.NUMBER -> Double::class.java
            OptionType.ATTACHMENT -> Attachment::class.java
            OptionType.UNKNOWN -> guessInputClass(outputClass)
            else -> error("Unknown option type $optionType")
        }
    }

    private fun guessInputClass(outputClass: Class<*>): Class<*> {
        if (outputClass == String::class.java) return String::class.java
        if (outputClass == Long::class.java) return Long::class.java
        if (outputClass == Int::class.java) return Long::class.java
        if (Number::class.java.isAssignableFrom(outputClass)) return Double::class.java
        if (outputClass == Boolean::class.java) return Boolean::class.java
        if (outputClass == User::class.java) return User::class.java
        if (outputClass == Member::class.java) return Member::class.java
        if (Channel::class.java.isAssignableFrom(outputClass)) return Channel::class.java
        if (outputClass == Role::class.java) return Role::class.java
        if (outputClass == Attachment::class.java) return Attachment::class.java
        if (IMentionable::class.java.isAssignableFrom(outputClass)) return IMentionable::class.java
        error("Cannot guess input class for output class " + outputClass.getName())
    }

    private fun createChoices(
        choices: Array<out Choice>,
        optionType: OptionType?,
        rootKey: String?
    ): Array<Command.Choice?> {
        require(choices.size <= OptionData.MAX_CHOICES) { "Cannot have more than " + OptionData.MAX_CHOICES + " choices" }
        val jdaChoices = arrayOfNulls<Command.Choice>(choices.size)
        for (i in choices.indices) {
            val choice = choices[i]
            val id = rootKey + choice.value + ".name"
            val name = i18n.getSingle(id, i18n.defaultLocale)
            check(name !is UnknownTranslation) { "Missing translation for choice $id" }
            if (optionType == OptionType.INTEGER) jdaChoices[i] = Command.Choice(name.get(), choice.intValue)
            else if (optionType == OptionType.STRING) jdaChoices[i] = Command.Choice(name.get(), choice.stringValue)
            else if (optionType == OptionType.NUMBER) jdaChoices[i] = Command.Choice(name.get(), choice.numberValue)
            else error("Cannot use @Choice on option of type $optionType")
        }
        return jdaChoices
    }
}
