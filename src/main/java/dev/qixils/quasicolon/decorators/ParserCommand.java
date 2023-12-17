/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators;

import dev.qixils.quasicolon.CommandManager;
import dev.qixils.quasicolon.cogs.impl.AbstractCommand;
import dev.qixils.quasicolon.converter.Converter;
import dev.qixils.quasicolon.locale.Context;
import dev.qixils.quasicolon.text.Text;
import dev.qixils.quasicolon.utils.QuasiMessage;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Constructor;
import java.util.concurrent.CompletableFuture;

abstract class ParserCommand<I extends CommandInteraction> extends AbstractCommand<I> {

	protected final AnnotationParser parser;

	protected ParserCommand(@NonNull AnnotationParser parser, @Nullable CommandData commandData, @NonNull Class<I> interactionClass) {
		super(commandData, interactionClass);
		this.parser = parser;
	}

	protected Converter<?, ?> createConverter(Class<? extends Converter<?, ?>> converterClass) {
		CommandManager commandManager = parser.getCommandManager();
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
				if (argClass.isAssignableFrom(commandManager.getLibrary().getClass()))
					arg = commandManager.getLibrary();
				else if (argClass.isAssignableFrom(commandManager.getClass()))
					arg = commandManager;
				else
					throw new IllegalArgumentException("Converter constructor must take Quasicord as an argument");
				// construct
				try {
					return (Converter<?, ?>) constructor.newInstance(arg);
				} catch (Exception e) {
					throw new IllegalArgumentException("Failed to construct converter", e);
				}
			} else {
				throw new IllegalArgumentException("Converter constructor must have 0 or 1 parameters; see @"); // TODO: ?
			}
		}
		throw new IllegalArgumentException("Converter must have a no-arg or Quasicord constructor");
	}

	public static void consumeCommandResult(@NonNull CommandInteraction interaction, Object result) {
		switch (result) {
			case CompletableFuture<?> fut -> fut.thenAccept(res -> consumeCommandResult(interaction, res));
			// terminal cases:
			case null -> {}
			case QuasiMessage message -> message.text().asString(Context.fromInteraction(interaction)).subscribe(string -> {
				var action = interaction.reply(string);
				message.modifier().accept(action);
				action.queue();
			});
			case Text text -> text.asString(Context.fromInteraction(interaction)).subscribe(msg -> interaction.reply(msg).queue());
			case String text -> interaction.reply(text).queue();
			default -> throw new IllegalArgumentException("Unsupported response type: " + result.getClass().getName());
		}
	}
}
