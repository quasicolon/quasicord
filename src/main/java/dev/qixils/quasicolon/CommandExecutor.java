/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon;

import dev.qixils.quasicolon.cogs.Command;
import dev.qixils.quasicolon.error.UserError;
import dev.qixils.quasicolon.text.Text;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

import static dev.qixils.quasicolon.Key.library;
import static dev.qixils.quasicolon.locale.Context.fromInteraction;
import static dev.qixils.quasicolon.text.Text.single;

class CommandExecutor {
	private final @NonNull Quasicord library;

	public CommandExecutor(@NonNull Quasicord library) {
		this.library = library;
	}

	private static void sendEphemeral(@NonNull IReplyCallback event, @NonNull Text text) {
		text.asString(fromInteraction(event)).subscribe(string -> event.reply(string).setEphemeral(true).queue());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@SubscribeEvent
	public void onCommandInteraction(@NonNull CommandInteraction event) {
		Optional.ofNullable( library.commands.get(event.getFullCommandName()) )
				.ifPresentOrElse(slash_cmd -> {
					try {
						((Command) slash_cmd).accept(event);
					} catch (UserError e) {
						sendEphemeral(event, e);
					} catch (Exception e) {
						library.getLogger().error("Failed to execute command " + slash_cmd.getName(), e);
						sendEphemeral(event, single(library("exception.command_error")));
					}
				}, () -> {
					library.getLogger().error("Could not find an executor for command " + event.getFullCommandName());
					sendEphemeral(event, single(library("exception.command_error")));
				});
	}
}
