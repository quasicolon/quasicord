/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs;

import net.dv8tion.jda.api.entities.Guild;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

/**
 * A {@link Cog} that applies to only one guild.
 * <p>
 * Guild cogs have no specific registration method. It is recommended that implementations of this
 * interface automatically register their associated commands upon construction.
 */
public interface GuildCog extends Cog {

	/**
	 * Gets the ID of the guild that this cog is for.
	 *
	 * @return guild ID
	 */
	long getGuildId();

	/**
	 * Gets the {@link Guild} that this cog is for.
	 *
	 * @return optional guild
	 */
	@NonNull
	default Optional<Guild> getGuild() {
		return Optional.ofNullable(getLibrary().getJDA().getGuildById(getGuildId()));
	}
}
