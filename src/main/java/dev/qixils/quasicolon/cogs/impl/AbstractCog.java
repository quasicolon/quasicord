package dev.qixils.quasicolon.cogs.impl;

import cloud.commandframework.Command;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.jda.JDACommandSender;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.meta.SimpleCommandMeta;
import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.cogs.Cog;
import dev.qixils.quasicolon.cogs.impl.autosend.CloudAutoSendHandler;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
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

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final AnnotationParser<JDACommandSender> cloudParser;
	protected final Quasicolon library;
	private final @NonNull List<CommandData> applicationCommands = new ArrayList<>();
	private boolean annotatedApplicationCommandsRegistered = false;
	private final @NonNull List<Command<JDACommandSender>> customCommands = new ArrayList<>();
	private boolean annotatedCustomCommandsRegistered = false;

	protected AbstractCog(@NonNull Quasicolon library) {
		this.library = library;
		this.cloudParser = new AnnotationParser<>(library.getCommandManager(), JDACommandSender.class, this::metaMapper);
		this.cloudParser.registerCommandExecutionMethodFactory(CloudAutoSendHandler.IS_AUTO_SEND, context -> CloudAutoSendHandler.of(library, context));
	}

	@NonNull
	protected CommandMeta metaMapper(@NonNull ParserParameters parameters) {
		return SimpleCommandMeta.empty();
	}

	@Override
	public @NonNull Collection<CommandData> getApplicationCommands() {
		if (!annotatedApplicationCommandsRegistered) {
			annotatedApplicationCommandsRegistered = true;
			for (Method method : getClass().getMethods()) {
				// TODO
			}
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

	protected void addApplicationCommand(@NonNull CommandData commandData) {
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
