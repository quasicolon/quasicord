/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators;

import dev.qixils.quasicolon.cogs.Cog;
import dev.qixils.quasicolon.cogs.Command;
import dev.qixils.quasicolon.cogs.impl.AbstractCommand;
import dev.qixils.quasicolon.converter.Converter;
import dev.qixils.quasicolon.converter.VoidConverter;
import dev.qixils.quasicolon.converter.VoidConverterImpl;
import dev.qixils.quasicolon.decorators.option.AutoCompleteFrom;
import dev.qixils.quasicolon.decorators.option.AutoCompleteWith;
import dev.qixils.quasicolon.decorators.option.ChannelTypes;
import dev.qixils.quasicolon.decorators.option.Choice;
import dev.qixils.quasicolon.decorators.option.Contextual;
import dev.qixils.quasicolon.decorators.option.ConvertWith;
import dev.qixils.quasicolon.decorators.option.Option;
import dev.qixils.quasicolon.decorators.option.Range;
import dev.qixils.quasicolon.decorators.slash.DefaultPermissions;
import dev.qixils.quasicolon.decorators.slash.SlashCommand;
import dev.qixils.quasicolon.locale.TranslationProvider;
import dev.qixils.quasicolon.locale.translation.SingleTranslation;
import dev.qixils.quasicolon.locale.translation.UnknownTranslation;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.context.ContextInteraction;
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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

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

	private Command<ContextInteraction<?>> parseApplicationCommand(Object object, Method method, ApplicationCommand annotation) {
		throw new UnsupportedOperationException("TODO"); // TODO
	}

	private Command<SlashCommandInteraction> parseSlashCommand(Object object, Method method, SlashCommand annotation) {
		// TODO: handle command groups (the SlashCommandGroup annotation and the dots in SlashCommand#value)
		//  ngl I have no ideas how to implement this right now... will need some sort of map or wrapper record or something

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
		ConverterData[] converters = new ConverterData[method.getParameterCount()];
		for (int i = 0; i < method.getParameterCount(); i++) {
			// set converter
			Parameter parameter = method.getParameters()[i];
			Contextual contextual = parameter.getAnnotation(Contextual.class);
			Option option = parameter.getAnnotation(Option.class);
			if (contextual != null && option != null)
				throw new IllegalArgumentException("Cannot have both @Contextual and @Option on the same parameter");
			ConvertWith convertWith = parameter.getAnnotation(ConvertWith.class);

			// converter data
			Converter<?, ?> converter;
			String optNameStr = null;

			if (convertWith != null) {
				converter = createConverter(convertWith.value());
			} else if (contextual != null) {
				if (Interaction.class.isAssignableFrom(parameter.getType()))
					converter = new VoidConverterImpl<>(Interaction.class, Function.identity());
				else {
					converter = cog.getLibrary().getRootRegistry().CONVERTER_REGISTRY.findConverter(Void.class, parameter.getType());
					if (converter == null)
						throw new IllegalArgumentException("No converter found for parameter " + parameter.getName() + " of type " + parameter.getType().getName());
				}
			} else if (option != null) {
				Class<?> inputClass = parseInputClass(parameter.getType(), option.type());
				converter = cog.getLibrary().getRootRegistry().CONVERTER_REGISTRY.findConverter(inputClass, parameter.getType());
				if (converter == null)
					throw new IllegalArgumentException("No converter found for parameter " + parameter.getName() + " of type " + parameter.getType().getName());
			} else {
				throw new IllegalArgumentException("Parameters must be annotated with @Contextual or @Option");
			}

			if (option != null) {
				// register option
				String optId = option.value();
				AutoCompleteWith acWith = parameter.getAnnotation(AutoCompleteWith.class);
				AutoCompleteFrom acFrom = parameter.getAnnotation(AutoCompleteFrom.class);
				Range range = parameter.getAnnotation(Range.class);
				ChannelTypes channelTypes = parameter.getAnnotation(ChannelTypes.class);
				Choice[] choices = parameter.getAnnotationsByType(Choice.class);

				// name
				SingleTranslation optName = i18n.getSingle(id + ".options." + optId + ".name", i18n.getDefaultLocale());
				if (optName instanceof UnknownTranslation)
					throw new IllegalStateException("Missing translation for option " + namespace + ":" + id + ".options." + optId + ".name");
				optNameStr = optName.get();

				// description
				SingleTranslation optDescription = i18n.getSingle(id + ".options." + optId + ".description", i18n.getDefaultLocale());
				if (optDescription instanceof UnknownTranslation)
					throw new IllegalStateException("Missing translation for option " + namespace + ":" + id + ".options." + optId + ".description");
				String optDescriptionStr = optDescription.get();

				// option
				OptionData opt = new OptionData(option.type(), optNameStr, optDescriptionStr, option.required(), acWith != null || acFrom != null);
				opt.setNameLocalizations(i18n.getDiscordTranslations(id + ".options." + optId + ".name"));
				opt.setDescriptionLocalizations(i18n.getDiscordTranslations(id + ".options." + optId + ".description"));

				// range
				if (range != null) {
					if (option.type() == OptionType.INTEGER)
						opt.setRequiredRange((long) range.min(), (long) range.max());
					else if (option.type() == OptionType.NUMBER)
						opt.setRequiredRange(range.min(), range.max());
					else if (option.type() == OptionType.STRING)
						opt.setRequiredLength((int) range.min(), (int) range.max());
					else
						throw new IllegalArgumentException("Cannot use @Range on option of type " + option.type());
				}

				// channel types
				if (channelTypes != null)
					opt.setChannelTypes(channelTypes.value());

				// choices
				if (choices.length > 0)
					opt.addChoices(createChoices(choices, option.type(), id + ".options." + optId + ".choices.", i18n));

				// TODO: auto complete

				command.addOptions(opt);
			}

			converters[i] = new ConverterData(converter, optNameStr);
		}

		return new AbstractCommand<>(command, SlashCommandInteraction.class) {
			@SuppressWarnings({"rawtypes", "unchecked"})
			@Override
			public void accept(@NonNull SlashCommandInteraction interaction) {
				// note: this has no try/catch because that is being handled at an even higher level than this

				// fetch args
				Object[] args = new Object[converters.length];
				for (int i = 0; i < args.length; i++) {
					ConverterData converterData = converters[i];
					Converter converter = converterData.converter();
					if (converter instanceof VoidConverter<?> voidConverter) {
						args[i] = voidConverter.convert(interaction);
						continue;
					}
					String optName = Objects.requireNonNull(converterData.optName(), "optName should only be null for @Contextual parameters");
					OptionMapping option = interaction.getOption(optName);
					if (option == null) {
						args[i] = null;
						continue;
					}
					// TODO: generate input identically to the specification in #parseInputClass / #guessInputClass
					// i.e. if converter.getInputClass() == String.class, call option.getAsString()
					Object input = new Object(); // <-- placeholder, please remove :-)
					args[i] = converter.convert(interaction, input);
				}

				// invoke and handle
				try {
					ConsumeCommandResult(interaction, method.invoke(object, args));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	private void ConsumeCommandResult(@NonNull SlashCommandInteraction interaction, Object result) {
		switch (result) {
			case CompletableFuture<?> fut -> fut.thenAccept(res -> ConsumeCommandResult(interaction, res));
			// terminal cases:
			case null -> {}
			case String text -> interaction.reply(text).queue(); // TODO: QuasiMessage and translation
			default -> throw new IllegalArgumentException("Unsupported response type: " + result.getClass().getName());
		}
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

	private Class<?> parseInputClass(Class<?> outputClass, OptionType optionType) {
		return switch (optionType) {
			case STRING			-> String.class;
			case INTEGER		-> Long.class;
			case BOOLEAN		-> Boolean.class;
			case USER			-> outputClass == Member.class ? Member.class : User.class;
			case CHANNEL		-> Channel.class;
			case ROLE			-> Role.class;
			case MENTIONABLE	-> IMentionable.class;
			case NUMBER			-> Double.class;
			case ATTACHMENT		-> Message.Attachment.class;
			case UNKNOWN		-> guessInputClass(outputClass);
			default -> throw new IllegalArgumentException("Unknown option type " + optionType);
		};
	}

	private Class<?> guessInputClass(Class<?> outputClass) {
		if (outputClass == String.class)						return String.class;
		if (outputClass == Long.class)							return Long.class;
		if (outputClass == Integer.class)						return Long.class;
		if (Number.class.isAssignableFrom(outputClass))			return Double.class;
		if (outputClass == Boolean.class)						return Boolean.class;
		if (outputClass == User.class)							return User.class;
		if (outputClass == Member.class)						return Member.class;
		if (Channel.class.isAssignableFrom(outputClass))		return Channel.class;
		if (outputClass == Role.class)							return Role.class;
		if (outputClass == Message.Attachment.class)			return Message.Attachment.class;
		if (IMentionable.class.isAssignableFrom(outputClass))	return IMentionable.class;
		throw new IllegalArgumentException("Cannot guess input class for output class " + outputClass.getName());
	}

	private net.dv8tion.jda.api.interactions.commands.Command.Choice[] createChoices(Choice[] choices, OptionType optionType, String rootKey, TranslationProvider i18n) {
		if (choices.length > OptionData.MAX_CHOICES)
			throw new IllegalArgumentException("Cannot have more than " + OptionData.MAX_CHOICES + " choices");
		net.dv8tion.jda.api.interactions.commands.Command.Choice[] jdaChoices = new net.dv8tion.jda.api.interactions.commands.Command.Choice[choices.length];
		for (int i = 0; i < choices.length; i++) {
			Choice choice = choices[i];
			String id = rootKey + choice.id() + ".name";
			SingleTranslation name = i18n.getSingle(id, i18n.getDefaultLocale());
			if (name instanceof UnknownTranslation)
				throw new IllegalStateException("Missing translation for choice " + id);
			if (optionType == OptionType.INTEGER)
				jdaChoices[i] = new net.dv8tion.jda.api.interactions.commands.Command.Choice(name.get(), choice.intValue());
			else if (optionType == OptionType.STRING)
				jdaChoices[i] = new net.dv8tion.jda.api.interactions.commands.Command.Choice(name.get(), choice.stringValue());
			else if (optionType == OptionType.NUMBER)
				jdaChoices[i] = new net.dv8tion.jda.api.interactions.commands.Command.Choice(name.get(), choice.numberValue());
			else
				throw new IllegalArgumentException("Cannot use @Choice on option of type " + optionType);
		}
		return jdaChoices;
	}
}
