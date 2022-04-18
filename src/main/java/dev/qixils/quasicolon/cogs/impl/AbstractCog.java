/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl;

import cloud.commandframework.Command;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.jda.JDACommandSender;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.meta.SimpleCommandMeta;
import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.cogs.ApplicationCommand;
import dev.qixils.quasicolon.cogs.Cog;
import dev.qixils.quasicolon.cogs.impl.decorators.cloud.CloudAutoSendHandler;
import dev.qixils.quasicolon.cogs.impl.decorators.jda.JdaAnnotationParser;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A basic implementation of a cog.
 * <p>
 * While extensions of this class are allowed, you should generally extend {@link AbstractGlobalCog}
 * or {@link AbstractGuildCog} instead.
 */
public abstract class AbstractCog implements Cog {

	protected final @NonNull Logger logger = LoggerFactory.getLogger(getClass());
	protected final @NonNull JdaAnnotationParser jdaParser;
	protected final @NonNull AnnotationParser<JDACommandSender> cloudParser;
	protected final @NonNull Quasicolon library;
	private final @NonNull List<ApplicationCommand<?>> applicationCommands = new ArrayList<>();
	private boolean annotatedApplicationCommandsRegistered = false;
	private final @NonNull List<Command<JDACommandSender>> customCommands = new ArrayList<>();
	private boolean annotatedCustomCommandsRegistered = false;

	protected AbstractCog(@NonNull Quasicolon library) {
		this.library = library;
		this.jdaParser = new JdaAnnotationParser();
		this.cloudParser = new AnnotationParser<>(library.getCommandManager(), JDACommandSender.class, this::metaMapper);
		this.cloudParser.registerCommandExecutionMethodFactory(CloudAutoSendHandler.IS_AUTO_SEND, context -> CloudAutoSendHandler.of(library, context));
	}

	@NotNull
	@Override
	public Quasicolon getLibrary() {
		return library;
	}

	@NonNull
	protected CommandMeta metaMapper(@NonNull ParserParameters parameters) {
		return SimpleCommandMeta.empty();
	}

	@Override
	public @NonNull Collection<ApplicationCommand<?>> getApplicationCommands() {
		if (!annotatedApplicationCommandsRegistered) {
			annotatedApplicationCommandsRegistered = true;
			applicationCommands.addAll(jdaParser.parse(this));
		}
		return applicationCommands;
	}

	@Override
	public @NonNull Collection<Command<JDACommandSender>> getCustomCommands() {
		if (!annotatedCustomCommandsRegistered) {
			annotatedCustomCommandsRegistered = true;
			customCommands.addAll(cloudParser.parse(this));
		}
		return customCommands;
	}

	protected void addApplicationCommand(@NonNull ApplicationCommand<?> commandData) {
		applicationCommands.add(commandData);
	}

	protected void addCustomCommand(Command.@NonNull Builder<JDACommandSender> command) {
		customCommands.add(command.build());
	}

	protected boolean areAnnotatedApplicationCommandsRegistered() {
		return annotatedApplicationCommandsRegistered;
	}

	protected boolean areAnnotatedCustomCommandsRegistered() {
		return annotatedCustomCommandsRegistered;
	}
}
