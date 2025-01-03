/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.db.collection

import dev.qixils.quasicord.db.CollectionName
import dev.qixils.quasicord.db.Index
import dev.qixils.quasicord.db.IndexKey
import org.bson.codecs.pojo.annotations.BsonProperty
import org.jetbrains.annotations.ApiStatus
import java.time.ZoneId

/**
 * An entry in the timezone configuration database collection.
 * This stores the selected timezone for a user.
 */
@CollectionName("timezone")
@Index(true, IndexKey("snowflake"))
data class TimeZoneConfig
/**
 * Constructs a new TimeZoneConfig entry.
 *
 * @param id the user's snowflake ID
 * @param timeZoneCode the user's configured timezone
 */ @ApiStatus.Internal constructor(
    /**
     * Gets the snowflake/ID of this config.
     *
     * @return snowflake/ID
     */
    val snowflake: Long,
    /**
     * Gets the timezone of this config.
     *
     * @return timezone
     */
    @BsonProperty("tzCode") val timeZone: ZoneId,
)
