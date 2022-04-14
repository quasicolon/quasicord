package dev.qixils.quasicolon.cogs.impl;

import cloud.commandframework.Command;
import cloud.commandframework.jda.JDACommandSender;
import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.cogs.Cog;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractCog implements Cog {

	protected final Quasicolon library;
	protected @NonNull List<CommandData> applicationCommands = new ArrayList<>();
	private boolean annotatedApplicationCommandsRegistered = false;
	protected @NonNull List<Command.Builder<JDACommandSender>> customCommands = new ArrayList<>();
	private boolean annotatedCustomCommandsRegistered = false;

	protected AbstractCog(@NonNull Quasicolon library) {
		this.library = library;
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
	public @NonNull Collection<Command.Builder<JDACommandSender>> getCustomCommands() {
		if (!annotatedCustomCommandsRegistered) {
			annotatedCustomCommandsRegistered = true;
			for (Method method : getClass().getMethods()) {
				// TODO
			}
		}
		return customCommands;
	}
}
