/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.db;

import net.dv8tion.jda.api.entities.ISnowflake;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an entity whose snowflake can be set.
 * <p>
 * This should be used for database objects with no-arg constructors, not for entities with
 * dynamically-updating IDs.
 */
public interface ISettableSnowflake extends ISnowflake {
	/**
	 * Sets the Snowflake ID of this entity. This is unique to every entity and will never change.
	 *
	 * @param id non-null String containing the ID.
	 */
	default void setId(@NotNull String id) {
		setIdLong(Long.parseLong(id));
	}

	/**
	 * Sets the Snowflake ID of this entity. This is unique to every entity and will never change.
	 *
	 * @param id long containing the ID.
	 */
	void setIdLong(long id);
}
