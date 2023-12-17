/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators;

import dev.qixils.quasicolon.CommandManager;
import dev.qixils.quasicolon.autocomplete.AutoCompleter;
import dev.qixils.quasicolon.autocomplete.AutoCompleterFrom;
import dev.qixils.quasicolon.cogs.SlashCommandDataBranch;
import dev.qixils.quasicolon.converter.Converter;
import dev.qixils.quasicolon.converter.VoidConverter;
import dev.qixils.quasicolon.converter.VoidConverterImpl;
import dev.qixils.quasicolon.decorators.option.*;
import dev.qixils.quasicolon.locale.TranslationProvider;
import dev.qixils.quasicolon.locale.translation.SingleTranslation;
import dev.qixils.quasicolon.locale.translation.UnknownTranslation;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.function.Function;

class ParserSlashCommand extends ParserCommand<SlashCommandInteraction> {

	private final String id;
	private final TranslationProvider i18n;
	private final SlashCommandDataBranch branch;
	private final ConverterData[] converters;
	private final Object object;
	private final Method method;

	public ParserSlashCommand(String id, AnnotationParser parser, TranslationProvider i18n, SlashCommandDataBranch branch, Object object, Method method) {
		super(parser, branch.rootIfStandalone(), SlashCommandInteraction.class);
		this.id = id;
		CommandManager commandManager = parser.getCommandManager();
		this.i18n = i18n;
		String namespace = i18n.getNamespace();
		this.branch = branch;
		this.object = object;
		this.method = method;

		// parameters
		converters = new ConverterData[method.getParameterCount()];
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
					converter = commandManager.getLibrary().getRootRegistry().CONVERTER_REGISTRY.findConverter(Void.class, parameter.getType());
					if (converter == null)
						throw new IllegalArgumentException("No converter found for parameter " + parameter.getName() + " of type " + parameter.getType().getName());
				}
			} else if (option != null) {
				Class<?> inputClass = parseInputClass(parameter.getType(), option.type());
				converter = commandManager.getLibrary().getRootRegistry().CONVERTER_REGISTRY.findConverter(inputClass, parameter.getType());
				if (converter == null)
					throw new IllegalArgumentException("No converter found for parameter " + parameter.getName() + " of type " + parameter.getType().getName());
			} else {
				throw new IllegalArgumentException("Parameters must be annotated with @Contextual or @Option");
			}

			if (option != null) {
				// register option
				String optId = option.value();
				String fullOptId = id + ".options." + optId;
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
				if (choices.length > 0) {
					if (!opt.getType().canSupportChoices())
						throw new IllegalArgumentException("Cannot use @Choice on option of type " + option.type());
					opt.addChoices(createChoices(choices, option.type(), id + ".options." + optId + ".choices."));
				}

				// auto complete
				if (acWith != null && acFrom != null)
					throw new IllegalArgumentException("Cannot have both @AutoCompleteWith and @AutoCompleteFrom on the same command");
				if (acWith != null || acFrom != null) {
					if (!option.type().canSupportChoices())
						throw new IllegalArgumentException("Cannot use @Choice on option of type " + option.type());
				}
				if (acWith != null) {
					AutoCompleter autoCompleter = parser.registerAutoCompleter(acWith.value());
					parser.putAutoCompleter(fullOptId, autoCompleter);
				} else if (acFrom != null) {
					var autocompletes = createChoices(acFrom.value(), option.type(), fullOptId + ".choices.");
					AutoCompleter autoCompleter = new AutoCompleterFrom(autocompletes);
					parser.putAutoCompleter(fullOptId, autoCompleter);
				}

				SubcommandData subcommand = branch.subcommand();
				if (subcommand != null)
					subcommand.addOptions(opt);
				else
					branch.root().addOptions(opt);
			}

			converters[i] = new ConverterData(converter, optNameStr);
		}
	}

	@Override
	public @NonNull String getName() {
		return id;
	}

	@Override
	public @NonNull String getDiscordName() {
		return branch.name();
	}

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

			Class inputClass = converter.getInputClass();
			// maybe this would be better done with a pair of hashmaps between Class<?> and Converter<?, ?>?
			// and a method on the converter that converts from just the Interaction and OptionMapping.
			Object input; // extract the appropriate type from the interaction option for the converter:
			if (inputClass == String.class)  			 input = option.getAsString();
			else if (inputClass == Long.class)   			 input = option.getAsInt();
			else if (inputClass == Double.class)   	 		 input = option.getAsDouble();
			else if (inputClass == Boolean.class)  	 		 input = option.getAsBoolean();
			else if (inputClass == Channel.class)   		 input = option.getAsChannel();
			else if (inputClass == Role.class)    		     input = option.getAsRole();
			else if (inputClass == IMentionable.class) 		 input = option.getAsMentionable();
			else if (inputClass == Message.Attachment.class) input = option.getAsAttachment();
			else if (inputClass == User.class)				 input = option.getAsUser();
			else if (inputClass == Member.class)  {
				var maybe_member = option.getAsMember(); // I think this is the only case where it is nullable
				if (maybe_member == null) {
					throw new IllegalArgumentException("Member was not found in this guild for " + option.getName());
				} else {
					input = maybe_member;
				}
			}

			else throw new IllegalArgumentException("Could not accept interaction option of type " + option.getType() + " for a converter from " + inputClass.getName());
			args[i] = converter.convert(interaction, input);
		}

		// invoke and handle
		try {
			consumeCommandResult(interaction, method.invoke(object, args));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

	private net.dv8tion.jda.api.interactions.commands.Command.Choice[] createChoices(Choice[] choices, OptionType optionType, String rootKey) {
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
