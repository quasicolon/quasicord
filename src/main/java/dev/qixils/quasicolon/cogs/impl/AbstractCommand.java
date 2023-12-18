/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl;

import dev.qixils.quasicolon.cogs.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractCommand<I extends CommandInteraction> implements Command<I> {

	protected final @Nullable CommandData commandData;
	protected final @NonNull Class<I> interactionClass;
	protected final @Nullable String guildId;

	public AbstractCommand(@Nullable CommandData commandData, @NonNull Class<I> interactionClass, @Nullable String guildId) {
		this.commandData = commandData;
		this.interactionClass = interactionClass;
		this.guildId = guildId;
	}

	@Nullable
	@Override
	public CommandData getCommandData() {
		return commandData;
	}

	@NonNull
	@Override
	public Class<I> getInteractionClass() {
		return interactionClass;
	}

	@Nullable
	@Override
	public String getGuildId() {
		return guildId;
	}
}
