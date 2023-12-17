/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon;

import dev.qixils.quasicolon.cogs.Command;
import dev.qixils.quasicolon.decorators.AnnotationParser;
import dev.qixils.quasicolon.error.UserError;
import dev.qixils.quasicolon.text.Text;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

import static dev.qixils.quasicolon.Key.library;
import static dev.qixils.quasicolon.locale.Context.fromInteraction;
import static dev.qixils.quasicolon.text.Text.single;

public class CommandManager {
	@Getter
	private final @NonNull Quasicord library;
	protected final @NonNull Map<String, Command<?>> commands = new HashMap<>();
	private final AnnotationParser parser;
	private boolean initialUpsertDone = false;

	public CommandManager(@NonNull Quasicord library) {
		this.library = library;
		this.parser = new AnnotationParser(this);
	}

	private static void sendEphemeral(@NonNull IReplyCallback event, @NonNull Text text) {
		text.asString(fromInteraction(event)).subscribe(string -> event.reply(string).setEphemeral(true).queue());
	}

	@Nullable
	public Command<?> getCommand(String discordName) {
		return commands.get(discordName);
	}

	public void upsertCommands(JDA jda) {
		if (initialUpsertDone) return;
		initialUpsertDone = true;
		var updater = jda.updateCommands();
		for (Command<?> command : commands.values()) {
			CommandData cmd = command.getCommandData();
			if (cmd != null) {
				//noinspection ResultOfMethodCallIgnored
				updater.addCommands(cmd);
			}
		}
		updater.queue();
	}

	public void registerCommand(@NonNull Command<?> command) {
		commands.put(command.getDiscordName(), command);
		if (!initialUpsertDone) return;
		CommandData cmd = command.getCommandData();
		if (cmd == null) return;
		library.getJDA().upsertCommand(cmd).queue();
	}

	public void discoverCommands(@NonNull Object object) {
		parser.parse(object).forEach(this::registerCommand);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@SubscribeEvent
	public void onCommandInteraction(@NonNull GenericCommandInteractionEvent event) {
		Command command = commands.get(event.getFullCommandName());
		if (command == null) {
			library.getLogger().error("Could not find an executor for command " + event.getFullCommandName());
			sendEphemeral(event, single(library("exception.command_error")));
			return;
		}
		try {
			command.accept(event);
		} catch (UserError e) {
			sendEphemeral(event, e);
		} catch (Exception e) {
			library.getLogger().error("Failed to execute command " + event.getFullCommandName(), e);
			sendEphemeral(event, single(library("exception.command_error")));
		}
	}
}
