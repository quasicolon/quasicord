/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

/**
 * A wrapper for {@link CommandData} which defines an executor for the command.
 */
public interface ApplicationCommand<I extends CommandInteraction> extends Consumer<I> {

	/**
	 * Gets the data (i.e. the defined arguments) for the command.
	 *
	 * @return command data
	 */
	@NonNull
	CommandData getCommandData();

	/**
	 * Gets the class of the {@link CommandInteraction} object which the executor will use.
	 *
	 * @return class of the expected {@link CommandInteraction} object
	 */
	@NonNull
	Class<I> getInteractionClass();

	/**
	 * Executes the command.
	 *
	 * @param interaction the interaction object which triggered the command execution
	 */
	@Override
	void accept(@NonNull I interaction);
}
