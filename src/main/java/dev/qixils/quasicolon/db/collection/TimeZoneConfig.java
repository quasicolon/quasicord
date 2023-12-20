/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.db.collection;

import dev.qixils.quasicolon.db.CollectionName;
import net.dv8tion.jda.api.entities.ISnowflake;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.ZoneId;

/**
 * An entry in the timezone configuration database collection.
 * This stores the selected timezone for a user.
 */
@CollectionName(name = "timezone")
public class TimeZoneConfig implements ISnowflake {
	private long snowflake;
	private @Nullable String tzCode;

	/**
	 * No-arg constructor for MongoDB.
	 */
	TimeZoneConfig() {
	}

	/**
	 * Constructs a new TimeZoneConfig entry.
	 *
	 * @param snowflake     the user's snowflake ID
	 * @param tzCode the user's configured timezone
	 */
	public TimeZoneConfig(long snowflake, @NonNull String tzCode) {
		this.snowflake = snowflake;
		this.tzCode = tzCode;
	}

	/**
	 * Constructs a new TimeZoneConfig entry.
	 *
	 * @param snowflake the user's snowflake ID
	 * @param tz the user's configured timezone
	 */
	public TimeZoneConfig(long snowflake, @NonNull ZoneId tz) {
		this(snowflake, tz.getId());
	}

	@Override
	public long getIdLong() {
		return snowflake;
	}

	public @NonNull String getTimeZoneCode() {
		if (tzCode == null)
			throw new IllegalStateException("Language code is null");
		return tzCode;
	}

	public @NonNull ZoneId getTimeZone() {
		if (tzCode == null)
			throw new IllegalStateException("Time zone code is null");
		return ZoneId.of(tzCode);
	}
}
