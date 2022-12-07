/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators;

import dev.qixils.quasicolon.cogs.Cog;
import dev.qixils.quasicolon.cogs.Command;
import dev.qixils.quasicolon.converter.Converter;
import dev.qixils.quasicolon.converter.ConverterImpl;
import dev.qixils.quasicolon.decorators.option.Contextual;
import dev.qixils.quasicolon.decorators.option.ConvertWith;
import dev.qixils.quasicolon.decorators.option.Option;
import dev.qixils.quasicolon.decorators.slash.DefaultPermissions;
import dev.qixils.quasicolon.decorators.slash.SlashCommand;
import dev.qixils.quasicolon.locale.TranslationProvider;
import dev.qixils.quasicolon.locale.translation.SingleTranslation;
import dev.qixils.quasicolon.locale.translation.UnknownTranslation;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class AnnotationParser {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationParser.class);
	private final @NonNull Cog cog;

	public AnnotationParser(@NonNull Cog cog) {
		this.cog = cog;
	}

	public Collection<Command<?>> parse(Object object) {
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

	private Command<?> parseApplicationCommand(Object object, Method method, ApplicationCommand annotation) {
		throw new UnsupportedOperationException("TODO");
	}

	private Command<?> parseSlashCommand(Object object, Method method, SlashCommand annotation) {
		// TODO: command groups (for i18n ID and, well, grouping)

		// get i18n
		String namespace = cog.getNamespace();
		String id = annotation.value();
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
		DefaultPermissions perms = method.getAnnotation(DefaultPermissions.class);
		if (perms != null) {
			Permission[] value = perms.value();
			command.setDefaultPermissions(value.length == 0
					? DefaultMemberPermissions.DISABLED
					: DefaultMemberPermissions.enabledFor(value));
		}

		// localizations
		command.setNameLocalizations(i18n.getDiscordTranslations(id + ".name"));
		command.setDescriptionLocalizations(i18n.getDiscordTranslations(id + ".description"));

		// parameters
		Converter<?, ?>[] converters = new Converter<?, ?>[method.getParameterCount()];
		for (int i = 0; i < method.getParameterCount(); i++) {
			Parameter parameter = method.getParameters()[i];
			Contextual contextual = parameter.getAnnotation(Contextual.class);
			Option option = parameter.getAnnotation(Option.class);
			if (contextual != null && option != null)
				throw new IllegalArgumentException("Cannot have both @Contextual and @Option on the same parameter");
			ConvertWith convertWith = parameter.getAnnotation(ConvertWith.class);
			if (convertWith != null) {
				converters[i] = createConverter(convertWith.value());
			} else if (contextual != null) {
				if (Interaction.class.isAssignableFrom(parameter.getType()))
					converters[i] = ConverterImpl.identity(Interaction.class);
				else {
					Converter<Void, ?> converter = cog.getLibrary().getRootRegistry().CONVERTER_REGISTRY.findConverter(Void.class, parameter.getType());
					if (converter == null)
						throw new IllegalArgumentException("No converter found for parameter " + parameter.getName() + " of type " + parameter.getType().getName());
					converters[i] = converter;
				}
			} else if (option != null) {
				// TODO
			} else {
				throw new IllegalArgumentException("Parameters must be annotated with @Contextual or @Option");
			}
		}

		// TODO
	}

	private Converter<?, ?> createConverter(Class<? extends Converter<?, ?>> converterClass) {
		for (Constructor<?> constructor : converterClass.getConstructors()) {
			if (constructor.getParameterCount() == 0) {
				try {
					return (Converter<?, ?>) constructor.newInstance();
				} catch (Exception e) {
					throw new IllegalArgumentException("Failed to construct converter", e);
				}
			} else if (constructor.getParameterCount() == 1) {
				Class<?> argClass = constructor.getParameterTypes()[0];
				// get arg to construct with
				Object arg;
				if (argClass.isAssignableFrom(cog.getLibrary().getClass()))
					arg = cog.getLibrary();
				else if (argClass.isAssignableFrom(cog.getClass()))
					arg = cog;
				else
					throw new IllegalArgumentException("Converter constructor must take Quasicord or Cog as an argument");
				// construct
				try {
					return (Converter<?, ?>) constructor.newInstance(arg);
				} catch (Exception e) {
					throw new IllegalArgumentException("Failed to construct converter", e);
				}
			} else {
				throw new IllegalArgumentException("Converter constructor must have 0 or 1 parameters; see @");
			}
		}
		throw new IllegalArgumentException("Converter must have a no-arg or Quasicord/Cog constructor");
	}
}
