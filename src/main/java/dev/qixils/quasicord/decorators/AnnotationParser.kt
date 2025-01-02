/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators

import dev.qixils.quasicord.CommandManager
import dev.qixils.quasicord.autocomplete.AutoCompleter
import dev.qixils.quasicord.cogs.Command
import dev.qixils.quasicord.cogs.SlashCommandDataBranch
import dev.qixils.quasicord.cogs.impl.SlashCommandDataBranchImpl
import dev.qixils.quasicord.decorators.slash.DefaultPermissions
import dev.qixils.quasicord.decorators.slash.SlashCommand
import dev.qixils.quasicord.decorators.slash.SlashSubCommand
import dev.qixils.quasicord.locale.TranslationProvider.Companion.getInstance
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class AnnotationParser(val commandManager: CommandManager) {
    private val autoCompleters: MutableMap<Class<out AutoCompleter?>?, AutoCompleter> =
        HashMap<Class<out AutoCompleter?>?, AutoCompleter>()
    private val autoCompletersByCommand: MutableMap<String?, AutoCompleter?> = HashMap<String?, AutoCompleter?>()

    init {
        commandManager.library.jda.addEventListener(this)
    }

    private fun createContextCommandData(
        annotation: ContextCommand,
        owner: AnnotatedElement,
        parent: AnnotatedElement?
    ): CommandData {
        val id = annotation.value
        val namespace = getNamespace(owner, parent)
        val i18n = getInstance(namespace)
        val name = i18n.getSingleDefaultOrThrow("$id.name").get()
        val command = Commands.context(annotation.type, name)
		command.isGuildOnly = annotation.guildOnly
		command.isNSFW = annotation.ageRestricted
        command.setNameLocalizations(i18n.getDiscordTranslations("$id.name"))

        // default permissions
        val perms = owner.getAnnotation<DefaultPermissions?>(DefaultPermissions::class.java)
        if (perms != null) {
            val value = perms.value
			command.defaultPermissions = if (value.isEmpty())
				DefaultMemberPermissions.DISABLED
			else
				DefaultMemberPermissions.enabledFor(*value)
        }

        return command
    }

    private fun createSlashCommandData(
        annotation: SlashCommand,
        owner: AnnotatedElement,
        parent: AnnotatedElement?
    ): SlashCommandData {
        val id: String? = annotation.value
        val namespace = getNamespace(owner, parent)
        val i18n = getInstance(namespace)
        val name = i18n.getSingleDefaultOrThrow("$id.name").get()
        val description = i18n.getSingleDefaultOrThrow("$id.description").get()
        val command = Commands.slash(name, description)
		command.isGuildOnly = annotation.guildOnly
		command.isNSFW = annotation.ageRestricted
        command.setNameLocalizations(i18n.getDiscordTranslations("$id.name"))
        command.setDescriptionLocalizations(i18n.getDiscordTranslations("$id.description"))

        // default permissions
        val perms = owner.getAnnotation<DefaultPermissions?>(DefaultPermissions::class.java)
        if (perms != null) {
            val value = perms.value
			command.defaultPermissions = if (value.isEmpty())
				DefaultMemberPermissions.DISABLED
			else
				DefaultMemberPermissions.enabledFor(*value)
        }

        return command
    }

    private fun createSlashSubCommandData(
        command: SlashCommandData,
        id: String,
        owner: AnnotatedElement,
        parent: AnnotatedElement?
    ): SlashCommandDataBranch {
        val parts = id.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        require(!(parts.size < 2 || parts.size > 3)) { "Subcommand ID " + id + " should be 2-3 parts long, not " + parts.size }
        val namespace = getNamespace(owner, parent)
        val i18n = getInstance(namespace)
        val name = i18n.getSingleDefaultOrThrow("$id.name").get()
        val description = i18n.getSingleDefaultOrThrow("$id.description").get()
        val subcommand = SubcommandData(name, description)
        subcommand.setNameLocalizations(i18n.getDiscordTranslations("$id.name"))
        subcommand.setDescriptionLocalizations(i18n.getDiscordTranslations("$id.description"))

        if (parts.size == 2) {
            command.addSubcommands(subcommand)
            return SlashCommandDataBranchImpl(command, null, subcommand)
        } else {
            val groupId = parts[0] + '.' + parts[1]
            val groupName = i18n.getSingleDefaultOrThrow("$groupId.name").get()
            val group =
                command.subcommandGroups.stream().filter { it.name == groupName }
                    .findFirst().orElseGet {
						val groupDescription = i18n.getSingleDefaultOrThrow("$groupId.description").get()
						val g = SubcommandGroupData(groupName, groupDescription)
						g.setNameLocalizations(i18n.getDiscordTranslations("$groupId.name"))
						g.setDescriptionLocalizations(i18n.getDiscordTranslations("$groupId.description"))
						command.addSubcommandGroups(g)
						g
					}
			group.addSubcommands(subcommand)
            return SlashCommandDataBranchImpl(command, group, subcommand)
        }
    }

    fun parse(`object`: Any): MutableCollection<Command<*>?> {
        commandManager.library.jda.addEventListener(`object`)
        commandManager.library.eventDispatcher.registerListeners(`object`)

        val parentClass: Class<*> = `object`.javaClass
        val parentCommandData: SlashCommand? = parentClass.getAnnotation(SlashCommand::class.java)
        val parentCommand =
			if (parentCommandData == null) null
        	else createSlashCommandData(parentCommandData, parentClass, null)
        val parentGuild: Guild? = parentClass.getAnnotation(Guild::class.java)

        val commands: MutableList<Command<*>?> = ArrayList<Command<*>?>()
        for (method in `object`.javaClass.getDeclaredMethods()) {
            val contextAnnotation: ContextCommand? = method.getAnnotation(ContextCommand::class.java)
            val slashAnnotation: SlashCommand? = method.getAnnotation(SlashCommand::class.java)
            val slashSubAnnotation: SlashSubCommand? = method.getAnnotation(SlashSubCommand::class.java)
            val guildId = (method.getAnnotation(Guild::class.java) ?: parentGuild)?.value

            val nonnull = listOfNotNull(contextAnnotation, slashAnnotation, slashSubAnnotation).count()
            if (nonnull == 0) continue
            require(nonnull <= 1) { "Cannot have multiple of @ContextCommand, @SlashCommand, and @SlashSubCommand on the same method" }

            require(Modifier.isPublic(method.modifiers)) { "Command method must be public" }
            require(!Modifier.isStatic(method.modifiers)) { "Decorator methods must not be static" }

            try {
                if (contextAnnotation != null) commands.add(
                    parseContextCommand(
                        `object`,
                        method,
                        contextAnnotation,
                        guildId
                    )
                )
                else if (slashAnnotation != null) commands.add(
                    parseSlashCommand(
                        `object`,
                        method,
                        slashAnnotation,
                        guildId
                    )
                )
                else if (slashSubAnnotation != null) {
                    requireNotNull(parentCommandData) { "@SlashSubCommand was applied to method " + method.name + ", but owning class " + `object`.javaClass.name + " lacks @SlashCommand" }
                    commands.add(
                        parseSlashSubCommand(
                            `object`,
                            method,
                            parentCommand!!,
                            parentCommandData,
                            slashSubAnnotation
                        )
                    )
                }
            } catch (e: Exception) {
                logger.warn("Failed to parse command " + method.name + " in " + `object`.javaClass.name, e)
            }
        }
        return commands
    }

    private fun getNamespace(vararg objects: AnnotatedElement?): String {
		for (obj in objects) {
			if (obj != null && obj.isAnnotationPresent(Namespace::class.java)) {
				return obj.getAnnotation<Namespace?>(Namespace::class.java).value
			}
		}
        return commandManager.library.namespace
    }

    private fun parseContextCommand(
        `object`: Any,
        method: Method,
        annotation: ContextCommand,
        guildId: String?
    ): Command<GenericContextInteractionEvent<*>> {
        val command = createContextCommandData(annotation, method, `object`.javaClass)
        return ParserContextCommand(annotation.value, this, command, `object`, method, guildId)
    }

    private fun parseSlashCommand(
        `object`: Any,
        method: Method,
        annotation: SlashCommand,
        guildId: String?
    ): Command<SlashCommandInteractionEvent> {
        val i18n = getInstance(getNamespace(method, `object`.javaClass))
        val command = createSlashCommandData(annotation, method, `object`.javaClass)
        val branch: SlashCommandDataBranch = SlashCommandDataBranchImpl(command, null, null)
        return ParserSlashCommand(annotation.value, this, i18n, branch, `object`, method, guildId)
    }

    private fun parseSlashSubCommand(
        `object`: Any,
        method: Method,
        command: SlashCommandData,
        parent: SlashCommand,
        annotation: SlashSubCommand
    ): Command<SlashCommandInteractionEvent> {
        val i18n = getInstance(getNamespace(method, `object`.javaClass))
        val id = parent.value + '.' + annotation.value
        val branch = createSlashSubCommandData(command, id, method, `object`.javaClass)
        return ParserSlashCommand(id, this, i18n, branch, `object`, method, null)
    }

    private fun createAutoCompleter(completerClass: Class<out AutoCompleter?>): AutoCompleter {
        // TODO: the duplication here is immense
        for (constructor in completerClass.constructors) {
            if (constructor.parameterCount == 0) {
                try {
                    return constructor.newInstance() as AutoCompleter
                } catch (e: Exception) {
                    throw IllegalArgumentException("Failed to construct auto-completer", e)
                }
            } else if (constructor.parameterCount == 1) {
                val argClass = constructor.parameterTypes[0]
                // get arg to construct with
                val arg: Any?
                if (argClass.isAssignableFrom(commandManager.library.javaClass)) arg = commandManager.library
                else if (argClass.isAssignableFrom(commandManager.javaClass)) arg = commandManager
                else throw IllegalArgumentException("Auto-completer constructor must take Quasicord as an argument")
                // construct
                try {
                    return constructor.newInstance(arg) as AutoCompleter
                } catch (e: Exception) {
                    throw IllegalArgumentException("Failed to construct auto-completer", e)
                }
            } else {
                throw IllegalArgumentException("Auto-completer constructor must have 0 or 1 parameters; see @") // TODO: ?
            }
        }
        throw IllegalArgumentException("Auto-completer must have a no-arg or Quasicord constructor")
    }

    fun registerAutoCompleter(autoCompleter: Class<out AutoCompleter?>?): AutoCompleter {
        return autoCompleters.computeIfAbsent(autoCompleter) { completerClass: Class<out AutoCompleter?>? ->
            this.createAutoCompleter(
                completerClass!!
            )
        }
    }

    fun putAutoCompleter(id: String?, ac: AutoCompleter?) {
        autoCompletersByCommand.put(id, ac)
    }

    @SubscribeEvent
    fun onAutoComplete(event: CommandAutoCompleteInteractionEvent) {
        // TODO: move to Command class maybe>?? also just like cleanup i think
        val guildId = if (event.guild != null) event.guild!!.id else null
        val command = commandManager.getCommand(event.fullCommandName, guildId)

        if (command == null) {
            event.replyChoices(listOf()).queue()
            return
        }

        val id = "${command.name}.options.${event.focusedOption.name}"
        val completer = autoCompletersByCommand[id]

        if (completer == null) {
            event.replyChoices(listOf()).queue()
            return
        }

        completer.getSuggestions(event).collectList().subscribe(
            { suggestions: List<net.dv8tion.jda.api.interactions.commands.Command.Choice> ->
                event.replyChoices(suggestions).queue()
            },
            { e: Throwable -> logger.error("Autocompleter threw exception handling {}", event, e) }
        )
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AnnotationParser::class.java)
    }
}
