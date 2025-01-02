/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.db.collection;

import dev.qixils.quasicord.db.CollectionName;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus;

import java.time.ZoneId;

/**
 * An entry in the timezone configuration database collection.
 * This stores the selected timezone for a user.
 */
@CollectionName("timezone")
public class TimeZoneConfig {

	@BsonId
	private final long snowflake; // _id
	private final @NonNull String tzCode;

	/**
	 * Constructs a new TimeZoneConfig entry.
	 *
	 * @param snowflake the user's snowflake ID
	 * @param tzCode    the user's configured timezone
	 */
	@ApiStatus.Internal
	@BsonCreator
	public TimeZoneConfig(
		@BsonId long snowflake,
		@BsonProperty("tzCode") @NonNull String tzCode
	) {
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

	/**
	 * Gets the snowflake/ID of this config.
	 *
	 * @return snowflake/ID
	 */
	@BsonId
	public long getId() {
		return snowflake;
	}

	/**
	 * Gets the timezone code of this config.
	 *
	 * @return timezone code
	 */
	@BsonProperty("tzCode")
	public @NonNull String getTimeZoneCode() {
		return tzCode;
	}

	/**
	 * Gets the timezone of this config.
	 *
	 * @return timezone
	 */
	@BsonIgnore
	public @NonNull ZoneId getTimeZone() {
		return ZoneId.of(tzCode);
	}
}
