/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord;

import dev.qixils.quasicord.cogs.Command;
import dev.qixils.quasicord.cogs.SlashCommand;
import dev.qixils.quasicord.decorators.AnnotationParser;
import dev.qixils.quasicord.error.UserError;
import dev.qixils.quasicord.text.Text;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static dev.qixils.quasicord.Key.library;
import static dev.qixils.quasicord.locale.Context.fromInteraction;
import static dev.qixils.quasicord.text.Text.single;

public class CommandManager {
	@Getter
	private final @NonNull Quasicord library;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	protected final @NonNull Map<@Nullable String, Map<String, Command<?>>> commands = new HashMap<>();
	private final AnnotationParser parser;
	private boolean initialUpsertDone = false;

	public CommandManager(@NonNull Quasicord library) {
		commands.put(null, new HashMap<>());
		this.library = library;
		this.parser = new AnnotationParser(this);
	}

	private static void sendEphemeral(@NonNull IReplyCallback event, @NonNull Text text) {
		text.asString(fromInteraction(event)).subscribe(string -> event.reply(string).setEphemeral(true).queue());
	}

	@Nullable
	public Command<?> getCommand(String discordName, @Nullable String guildId) {
		if (guildId != null && commands.containsKey(guildId)) {
			Command<?> command = commands.get(guildId).get(discordName);
			if (command != null) return command;
		}
		return commands.get(null).get(discordName);
	}

	public void upsertCommands(JDA jda) {
		if (initialUpsertDone) return;
		initialUpsertDone = true;
		logger.info("Upserting commands");
		for (Map.Entry<String, Map<String, Command<?>>> entry : commands.entrySet()) {
			String guildId = entry.getKey();
			Map<String, Command<?>> guildCommands = entry.getValue();

			CommandListUpdateAction updater;
			if (guildId == null) {
				updater = jda.updateCommands();
			} else {
				Guild guild = jda.getGuildById(guildId);
				if (guild == null)
					continue;
				updater = guild.updateCommands();
			}

			Set<String> rootSlashCommands = new HashSet<>();

			for (Command<?> command : guildCommands.values()) {
				CommandData commandData;
				if (command instanceof SlashCommand slashCommand) {
					commandData = slashCommand.getBranch().root();
					if (rootSlashCommands.contains(commandData.getName()))
						continue;
					rootSlashCommands.add(commandData.getName());
				} else {
					commandData = command.getCommandData();
				}

				if (commandData == null)
					continue; // i don't think this should happen but just in case
				logger.debug("Upserting command {} to guild {}", commandData.getName(), guildId);
				//noinspection ResultOfMethodCallIgnored
				updater.addCommands(commandData);
			}

			updater.queue();
		}
	}

	public void registerCommand(@NonNull Command<?> command) {
		String guildId = command.getGuildId();
		commands.computeIfAbsent(guildId, $ -> new HashMap<>()).put(command.getDiscordName(), command);
		if (!initialUpsertDone)
			return;

		CommandData cmd = command.getCommandData();
		if (cmd == null)
			return;

		JDA jda = library.getJDA();
		RestAction<net.dv8tion.jda.api.interactions.commands.Command> upsert;

		if (guildId == null) {
			upsert = jda.upsertCommand(cmd);
		} else {
			Guild guild = jda.getGuildById(guildId);
			if (guild == null)
				return;
			upsert = guild.upsertCommand(cmd);
		}

		upsert.queue();
	}

	public void discoverCommands(@NonNull Object object) {
		parser.parse(object).forEach(this::registerCommand);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@SubscribeEvent
	public void onCommandInteraction(@NonNull GenericCommandInteractionEvent event) {
		String guildId = event.getGuild() == null ? event.getGuild().getId() : null;
		Command command = getCommand(event.getFullCommandName(), guildId);
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
