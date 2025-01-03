/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.db.collection

import dev.qixils.quasicord.db.CollectionName
import dev.qixils.quasicord.db.Index
import dev.qixils.quasicord.db.IndexKey
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import org.jetbrains.annotations.ApiStatus
import java.util.*

/**
 * An entry in the locale configuration database collection.
 * This stores the selected locale for a user, channel, or guild.
 */
@CollectionName("locale")
@Index(IndexKey("snowflake"), IndexKey("entryType"))
data class LocaleConfig @ApiStatus.Internal constructor(
	/**
	 * Returns the database ID of this object.
	 *
	 * @return ID
	 */
	@BsonId
	val id: ObjectId,
	/**
	 * Returns the snowflake for this object.
	 *
	 * @return snowflake
	 */
	val snowflake: Long,
	/**
	 * Returns the type of this object.
	 *
	 * @return type
	 */
	val entryType: EntryType,
	/**
	 * Returns the code of the configured language of this object.
	 *
	 * @return language code
	 */
	@BsonProperty("languageCode")
	val language: Locale,
) {

    /**
     * Constructs a new LocaleConfig entry.
     *
     * @param snowflake the object's snowflake ID
     * @param entryType the type of entry
     * @param language  the object's configured language
     */
    constructor(snowflake: Long, entryType: EntryType, language: Locale) : this(ObjectId.get(), snowflake, entryType, language)

    /**
     * A type of entry used to distinguish between snowflake IDs of users, channels, and guilds.
     */
    enum class EntryType {

        /**
         * A user's entry.
         */
        USER,

        /**
         * A channel's entry.
         */
        CHANNEL,

        /**
         * A guild's entry.
         */
        GUILD
    }
}
