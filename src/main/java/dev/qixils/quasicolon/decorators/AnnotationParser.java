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
import dev.qixils.quasicolon.decorators.slash.SlashSubCommand;
import dev.qixils.quasicolon.locale.TranslationProvider;
import dev.qixils.quasicolon.locale.translation.SingleTranslation;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
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
import java.util.stream.Stream;

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
		String namespace = getNamespace(owner, parent);
		TranslationProvider i18n = TranslationProvider.getInstance(namespace);

		// basic data
		SingleTranslation name = i18n.getSingleDefaultOrThrow(id + ".name");
		SingleTranslation description = i18n.getSingleDefaultOrThrow(id + ".description");
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

	private SlashCommandDataBranch createSlashSubCommandData(@NotNull SlashCommandData command, @NotNull String id, @NotNull AnnotatedElement owner, @Nullable AnnotatedElement parent) {
		String[] parts = id.split("/");
		if (parts.length < 2 || parts.length > 3) {
			throw new IllegalArgumentException("Subcommand ID " + id + " should be 2-3 parts long, not " + parts.length);
		}
		String namespace = getNamespace(owner, parent);
		TranslationProvider i18n = TranslationProvider.getInstance(namespace);
		String name = i18n.getSingleDefaultOrThrow(id + ".name").get();
		String description = i18n.getSingleDefaultOrThrow(id + ".description").get();
		SubcommandData subcommand = new SubcommandData(name, description);
		subcommand.setNameLocalizations(i18n.getDiscordTranslations(id + ".name"));
		subcommand.setDescriptionLocalizations(i18n.getDiscordTranslations(id + ".description"));

		if (parts.length == 2) {
			command.addSubcommands(subcommand);
			return new SlashCommandDataBranchImpl(command, null, subcommand);
		} else {
			String groupId = parts[0] + '/' + parts[1];
			String groupName = i18n.getSingleDefaultOrThrow(groupId + ".name").get();
			SubcommandGroupData group = command.getSubcommandGroups().stream().filter(g -> g.getName().equals(groupName)).findFirst().orElseGet(() -> {
				String groupDescription = i18n.getSingleDefaultOrThrow(groupId + ".description").get();
				SubcommandGroupData g = new SubcommandGroupData(groupName, groupDescription);
				g.setNameLocalizations(i18n.getDiscordTranslations(groupId + ".name"));
				g.setDescriptionLocalizations(i18n.getDiscordTranslations(groupId + ".description"));
				command.addSubcommandGroups(g);
				return g;
			});
			group.addSubcommands(subcommand);
			return new SlashCommandDataBranchImpl(command, group, subcommand);
		}
	}

	public Collection<Command<?>> parse(Object object) {
		Class<?> parentClass = object.getClass();
		SlashCommand parentCommandData = parentClass.getAnnotation(SlashCommand.class);
		SlashCommandData parentCommand = parentCommandData == null
			? null
			: createSlashCommandData(parentCommandData, parentClass, null);

		List<Command<?>> commands = new ArrayList<>();
		for (final Method method : object.getClass().getDeclaredMethods()) {
			ApplicationCommand applicationAnnotation = method.getAnnotation(ApplicationCommand.class);
			SlashCommand slashAnnotation = method.getAnnotation(SlashCommand.class);
			SlashSubCommand slashSubAnnotation = method.getAnnotation(SlashSubCommand.class);

			long nonnull = Stream.of(applicationAnnotation, slashAnnotation, slashSubAnnotation)
				.filter(Objects::nonNull)
				.count();
			if (nonnull == 0)
				continue;
			if (nonnull > 1)
				throw new IllegalArgumentException("Cannot have multiple of @ApplicationCommand, @SlashCommand, and @SlashSubCommand on the same method");

			if (!Modifier.isPublic(method.getModifiers()))
				throw new IllegalArgumentException("Command method must be public");
			if (Modifier.isStatic(method.getModifiers()))
				throw new IllegalArgumentException("Decorator methods must not be static");

			try {
				if (applicationAnnotation != null)
					commands.add(parseApplicationCommand(object, method, applicationAnnotation));
				else if (slashAnnotation != null)
					commands.add(parseSlashCommand(object, method, slashAnnotation));
				else if (slashSubAnnotation != null) {
					if (parentCommandData == null)
						throw new IllegalArgumentException("@SlashSubCommand was applied to method " + method.getName() + ", but owning class " + object.getClass().getName() + " lacks @SlashCommand");
					commands.add(parseSlashSubCommand(object, method, parentCommand, parentCommandData, slashSubAnnotation));
				}
			} catch (Exception e) {
				logger.warn("Failed to parse command " + method.getName() + " in " + object.getClass().getName(), e);
			}
		}
		return commands;
	}

	private Command<ContextInteraction<?>> parseApplicationCommand(Object object, Method method, ApplicationCommand annotation) {
		throw new UnsupportedOperationException("TODO"); // TODO
	}

	@NonNull
	private String getNamespace(@Nullable AnnotatedElement @Nullable ... objects) {
		if (objects != null) {
			for (AnnotatedElement obj : objects) {
				if (obj != null && obj.isAnnotationPresent(Namespace.class)) {
					return obj.getAnnotation(Namespace.class).value();
				}
			}
		}
		return commandManager.getLibrary().getNamespace();
	}

	private Command<SlashCommandInteraction> parseSlashCommand(Object object, Method method, SlashCommand annotation) {
		TranslationProvider i18n = TranslationProvider.getInstance(getNamespace(method, object.getClass()));
		SlashCommandData command = createSlashCommandData(annotation, method, object.getClass());
		SlashCommandDataBranch branch = new SlashCommandDataBranchImpl(command, null, null);
		return new ParserCommand(annotation.value(), this, i18n, branch, object, method);
	}

	private Command<SlashCommandInteraction> parseSlashSubCommand(Object object, Method method, SlashCommandData command, SlashCommand parent, SlashSubCommand annotation) {
		TranslationProvider i18n = TranslationProvider.getInstance(getNamespace(method, object.getClass()));
		String id = parent.value() + '/' + annotation.value();
		SlashCommandDataBranch branch = createSlashSubCommandData(command, id, method, object.getClass());
		return new ParserCommand(id, this, i18n, branch, object, method);
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
		Command<?> command = commandManager.getCommand(event.getFullCommandName());

		Collection<net.dv8tion.jda.api.interactions.commands.Command.Choice> response = Collections.emptyList();
		if (command != null) {
			String id = command.getName() + ".options." + event.getFocusedOption().getName();
			AutoCompleter completer = autoCompletersByCommand.get(id);

			if (completer != null) {
				try {
					response = completer.getSuggestions(event);
				} catch (Exception e) {
					logger.error("Autocompleter threw exception handling " + event, e);
				}
			}
		}

		event.replyChoices(response).queue();
	}
}
