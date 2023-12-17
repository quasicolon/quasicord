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
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommand<I extends CommandInteraction> implements Command<I> {
	private final @Nullable CommandData commandData;
	private final @NonNull Class<I> interactionClass;

	public AbstractCommand(@Nullable CommandData commandData, @NonNull Class<I> interactionClass) {
		this.commandData = commandData;
		this.interactionClass = interactionClass;
	}

	@Nullable
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
