/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators;

import dev.qixils.quasicolon.CommandManager;
import dev.qixils.quasicolon.autocomplete.AutoCompleter;
import dev.qixils.quasicolon.cogs.Command;
import dev.qixils.quasicolon.cogs.SlashCommandDataBranch;
import dev.qixils.quasicolon.cogs.impl.SlashCommandDataBranchImpl;
import dev.qixils.quasicolon.decorators.slash.DefaultPermissions;
import dev.qixils.quasicolon.decorators.slash.SlashCommand;
import dev.qixils.quasicolon.locale.TranslationProvider;
import dev.qixils.quasicolon.locale.translation.SingleTranslation;
import dev.qixils.quasicolon.locale.translation.UnknownTranslation;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.context.ContextInteraction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public final class AnnotationParser {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationParser.class);
	private final @NonNull CommandManager commandManager;
	private final @NonNull Map<Class<? extends AutoCompleter>, AutoCompleter> autoCompleters = new HashMap<>();
	private final @NonNull Map<String, AutoCompleter> autoCompletersByCommand = new HashMap<>();

	public AnnotationParser(@NonNull CommandManager commandManager) {
		this.commandManager = commandManager;
		commandManager.getLibrary().getJDA().addEventListener(this);
	}

	@NonNull
	CommandManager getCommandManager() {
		return commandManager;
	}

	private SlashCommandData createSlashCommandData(@NotNull SlashCommand annotation, @NotNull AnnotatedElement owner, @Nullable AnnotatedElement parent) {
		String id = annotation.value();

		// get i18n
		String namespace;
		if (owner.isAnnotationPresent(Namespace.class))
			namespace = owner.getAnnotation(Namespace.class).value();
		else if (parent != null && parent.getClass().isAnnotationPresent(Namespace.class))
			namespace = parent.getClass().getAnnotation(Namespace.class).value();
		else
			namespace = commandManager.getLibrary().getNamespace();
		TranslationProvider i18n = TranslationProvider.getInstance(namespace);

		// name
		SingleTranslation name = i18n.getSingle(id + ".name", i18n.getDefaultLocale());
		if (name instanceof UnknownTranslation)
			throw new IllegalStateException("Missing translation for command " + namespace + ":" + id + ".name");

		// description
		SingleTranslation description = i18n.getSingle(id + ".description", i18n.getDefaultLocale());
		if (description instanceof UnknownTranslation)
			throw new IllegalStateException("Missing translation for command " + namespace + ":" + id + ".description");

		// basic data
		SlashCommandData command = Commands.slash(name.get(), description.get());
		command.setGuildOnly(annotation.guildOnly());
		command.setNSFW(annotation.ageRestricted());

		// default permissions
		DefaultPermissions perms = owner.getAnnotation(DefaultPermissions.class);
		if (perms != null) {
			Permission[] value = perms.value();
			command.setDefaultPermissions(value.length == 0
				? DefaultMemberPermissions.DISABLED
				: DefaultMemberPermissions.enabledFor(value));
		}

		// localizations
		command.setNameLocalizations(i18n.getDiscordTranslations(id + ".name"));
		command.setDescriptionLocalizations(i18n.getDiscordTranslations(id + ".description"));

		return command;
	}

	public Collection<Command<?>> parse(Object object) {
		Class<?> parentClass = object.getClass();
		SlashCommand parentCommandData = parentClass.getAnnotation(SlashCommand.class);
		CommandData parentCommand = parentCommandData == null
			? null
			: createSlashCommandData(parentCommandData, parentClass, null);

		List<Command<?>> commands = new ArrayList<>();
		for (final Method method : object.getClass().getDeclaredMethods()) {
			ApplicationCommand applicationAnnotation = method.getAnnotation(ApplicationCommand.class);
			SlashCommand slashAnnotation = method.getAnnotation(SlashCommand.class);
			if (applicationAnnotation == null && slashAnnotation == null)
				continue;
			if (applicationAnnotation != null && slashAnnotation != null)
				throw new IllegalArgumentException("Cannot have both @ApplicationCommand and @SlashCommand on the same method");

			if (!Modifier.isPublic(method.getModifiers()))
				throw new IllegalArgumentException("Command method must be public");
			if (Modifier.isStatic(method.getModifiers()))
				throw new IllegalArgumentException("Decorator methods must not be static");

			try {
				if (applicationAnnotation != null)
					commands.add(parseApplicationCommand(object, method, applicationAnnotation));
				else
					commands.add(parseSlashCommand(object, method, slashAnnotation));
			} catch (Exception e) {
				logger.warn("Failed to parse command " + method.getName() + " in " + object.getClass().getName(), e);
			}
		}
		return commands;
	}

	private Command<ContextInteraction<?>> parseApplicationCommand(Object object, Method method, ApplicationCommand annotation) {
		throw new UnsupportedOperationException("TODO"); // TODO
	}

	private Command<SlashCommandInteraction> parseSlashCommand(Object object, Method method, SlashCommand annotation) {
		// get i18n
		String namespace;
		if (method.isAnnotationPresent(Namespace.class))
			namespace = method.getAnnotation(Namespace.class).value();
		else if (object.getClass().isAnnotationPresent(Namespace.class))
			namespace = object.getClass().getAnnotation(Namespace.class).value();
		else
			namespace = commandManager.getLibrary().getNamespace();
		TranslationProvider i18n = TranslationProvider.getInstance(namespace);

		SlashCommandData command = createSlashCommandData(annotation, method, object.getClass());
		SlashCommandDataBranch branch = new SlashCommandDataBranchImpl(command, null, null);
		return new ParserCommand(this, i18n, branch, object, method);
	}

	private AutoCompleter createAutoCompleter(Class<? extends AutoCompleter> completerClass) {
		// TODO: the duplication here is immense
		for (Constructor<?> constructor : completerClass.getConstructors()) {
			if (constructor.getParameterCount() == 0) {
				try {
					return (AutoCompleter) constructor.newInstance();
				} catch (Exception e) {
					throw new IllegalArgumentException("Failed to construct auto-completer", e);
				}
			} else if (constructor.getParameterCount() == 1) {
				Class<?> argClass = constructor.getParameterTypes()[0];
				// get arg to construct with
				Object arg;
				if (argClass.isAssignableFrom(commandManager.getLibrary().getClass()))
					arg = commandManager.getLibrary();
				else if (argClass.isAssignableFrom(commandManager.getClass()))
					arg = commandManager;
				else
					throw new IllegalArgumentException("Auto-completer constructor must take Quasicord as an argument");
				// construct
				try {
					return (AutoCompleter) constructor.newInstance(arg);
				} catch (Exception e) {
					throw new IllegalArgumentException("Failed to construct auto-completer", e);
				}
			} else {
				throw new IllegalArgumentException("Auto-completer constructor must have 0 or 1 parameters; see @"); // TODO: ?
			}
		}
		throw new IllegalArgumentException("Auto-completer must have a no-arg or Quasicord constructor");
	}

	AutoCompleter registerAutoCompleter(Class<? extends AutoCompleter> autoCompleter) {
		return autoCompleters.computeIfAbsent(autoCompleter, this::createAutoCompleter);
	}

	void putAutoCompleter(String id, AutoCompleter ac) {
		autoCompletersByCommand.put(id, ac);
	}

	@SubscribeEvent
	public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
		// TODO: move to Command class maybe>?? also just like cleanup i think
		String id = event.getFullCommandName() /* TODO: wrong format! */ + ".options." + event.getFocusedOption().getName();
		AutoCompleter completer = autoCompletersByCommand.get(id);
		if (completer == null) {
			event.replyChoices(Collections.emptyList()).queue();
			return;
		}
		event.replyChoices(completer.getSuggestions(event)).queue();
	}
}
