/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators;

import dev.qixils.quasicolon.CommandManager;
import dev.qixils.quasicolon.converter.Converter;
import dev.qixils.quasicolon.converter.VoidConverterImpl;
import dev.qixils.quasicolon.decorators.option.ConvertWith;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.context.ContextInteraction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Function;

class ParserContextCommand extends ParserCommand<ContextInteraction> {

	private final String id;
	private final Converter<?, ?>[] converters;
	private final Object object;
	private final Method method;

	public ParserContextCommand(@NonNull String id, @NonNull AnnotationParser parser, @NonNull CommandData command, @NonNull Object object, @NonNull Method method, @Nullable String guildId) {
		super(parser, command, ContextInteraction.class, guildId);
		this.id = id;
		CommandManager commandManager = parser.getCommandManager();
		this.object = object;
		this.method = method;

		// parameters
		converters = new Converter<?, ?>[method.getParameterCount()];
		for (int i = 0; i < method.getParameterCount(); i++) {
			// set converter
			Parameter parameter = method.getParameters()[i];
			ConvertWith convertWith = parameter.getAnnotation(ConvertWith.class);

			// converter data
			Converter<?, ?> converter;

			if (convertWith != null) {
				converter = createConverter(convertWith.value());
			} else {
				if (Interaction.class.isAssignableFrom(parameter.getType()))
					converter = new VoidConverterImpl<>(Interaction.class, Function.identity());
				else {
					converter = commandManager.getLibrary().getRootRegistry().CONVERTER_REGISTRY.findConverter(Void.class, parameter.getType());
					if (converter == null)
						throw new IllegalArgumentException("No converter found for parameter " + parameter.getName() + " of type " + parameter.getType().getName());
				}
			}

			converters[i] = converter;
		}
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	public @NonNull CommandData getCommandData() {
		return super.getCommandData();
	}

	@Override
	public @NonNull String getName() {
		return id;
	}

	@Override
	public @NonNull String getDiscordName() {
		return getCommandData().getName();
	}

	@Override
	public void accept(@NonNull ContextInteraction interaction) {
		// note: this has no try/catch because that is being handled at an even higher level than this

		// fetch args
		Object[] args = new Object[converters.length];
		for (int i = 0; i < args.length; i++) {
			//noinspection DataFlowIssue
			args[i] = converters[i].convert(interaction, null);
		}

		// invoke and handle
		try {
			consumeCommandResult(interaction, method.invoke(object, args));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
