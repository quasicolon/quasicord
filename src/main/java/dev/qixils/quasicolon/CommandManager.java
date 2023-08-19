/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon;

import dev.qixils.quasicolon.cogs.Command;
import dev.qixils.quasicolon.error.UserError;
import dev.qixils.quasicolon.text.Text;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

import static dev.qixils.quasicolon.Key.library;
import static dev.qixils.quasicolon.locale.Context.fromInteraction;
import static dev.qixils.quasicolon.text.Text.single;

public class CommandManager {
	private final @NonNull Quasicord library;
	protected final @NonNull Map<String, Command<?>> commands = new HashMap<>();
	private boolean initialUpsertDone = false;

	public CommandManager(@NonNull Quasicord library) {
		this.library = library;
	}

	private static void sendEphemeral(@NonNull IReplyCallback event, @NonNull Text text) {
		text.asString(fromInteraction(event)).subscribe(string -> event.reply(string).setEphemeral(true).queue());
	}

	public void upsertCommands(JDA jda) {
		if (initialUpsertDone) return;
		initialUpsertDone = true;
		var updater = jda.updateCommands();
		for (Command<?> command : commands.values())
			//noinspection ResultOfMethodCallIgnored
			updater.addCommands(command.getCommandData());
		updater.queue();
	}

	public void registerCommand(@NonNull Command<?> command) {
		commands.put(command.getName(), command);
		if (initialUpsertDone)
			library.getJDA().upsertCommand(command.getCommandData()).queue();
	}

	public void discoverCommands(@NonNull Object object) {
		// TODO: use AnnotationParser
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@SubscribeEvent
	public void onCommandInteraction(@NonNull GenericCommandInteractionEvent event) {
		commands.entrySet().stream()
				.filter(entry -> entry.getKey().equals(event.getFullCommandName()))
				.findFirst()
				.ifPresentOrElse(entry -> {
					try {
						((Command) entry.getValue()).accept(event);
					} catch (UserError e) {
						sendEphemeral(event, e);
					} catch (Exception e) {
						library.getLogger().error("Failed to execute command " + entry.getKey(), e);
						sendEphemeral(event, single(library("exception.command_error")));
					}
				}, () -> {
					library.getLogger().error("Could not find an executor for command " + event.getFullCommandName());
					sendEphemeral(event, single(library("exception.command_error")));
				});
	}
}
