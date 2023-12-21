/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.cogs;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a branch of the command tree.
 * Describes the root command, and if respectively present, the subcommand and subcommand group.
 */
public interface SlashCommandDataBranch {

	/**
	 * The root command.
	 *
	 * @return the root command
	 */
	@NonNull SlashCommandData root();

	/**
	 * The subcommand group, if present.
	 *
	 * @return the subcommand group
	 */
    @Nullable SubcommandGroupData group();

	/**
	 * The subcommand, if present.
	 *
	 * @return the subcommand
	 */
    @Nullable SubcommandData subcommand();

	/**
	 * The full name of this command according to Discord.
	 *
	 * @return the command name
	 */
	@NonNull
	default String name() {
		String id = root().getName();

		SubcommandGroupData group = group();
		if (group != null) id += ' ' + group.getName();

		SubcommandData subcommand = subcommand();
		if (subcommand != null) id += ' ' + subcommand.getName();

		return id;
	}

	/**
	 * Returns the root command if this is a standalone command.
	 *
	 * @return the root command or null
	 */
	@Nullable
	default SlashCommandData rootIfStandalone() {
		if (subcommand() != null) return null;
		return root();
	}
}
