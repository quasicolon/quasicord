/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl;

import dev.qixils.quasicolon.cogs.ApplicationCommand;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractApplicationCommand<I extends CommandInteraction> implements ApplicationCommand<I> {
	private final @NonNull CommandData commandData;
	private final @NonNull Class<I> interactionClass;

	public AbstractApplicationCommand(@NonNull CommandData commandData, @NonNull Class<I> interactionClass) {
		this.commandData = commandData;
		this.interactionClass = interactionClass;
	}

	@NotNull
	@Override
	public CommandData getCommandData() {
		return commandData;
	}

	@NotNull
	@Override
	public Class<I> getInteractionClass() {
		return interactionClass;
	}
}
