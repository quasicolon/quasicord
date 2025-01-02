/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.db.collection;

import dev.qixils.quasicord.db.CollectionName;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;

/**
 * An entry in the locale configuration database collection.
 * This stores the selected locale for a user, channel, or guild.
 */
@CollectionName("locale")
public class LocaleConfig {

	@BsonId
	private final @NonNull String id;
	private final long snowflake;
	private final @NonNull EntryType entryType;
	private final @NonNull String languageCode;

	/**
	 * Creates an ID for this object.
	 *
	 * @param entryType the type of this object
	 * @param snowflake the snowflake of this object
	 * @return object ID
	 */
	@NonNull
	public static String createId(@NonNull EntryType entryType, long snowflake) {
		return entryType.name() + '/' + Long.toHexString(snowflake);
	}

	/**
	 * Constructor for MongoDB.
	 */
	@ApiStatus.Internal
	@BsonCreator
	public LocaleConfig(
		@BsonId @NonNull String id,
		@BsonProperty("snowflake") long snowflake,
		@BsonProperty("entryType") @NonNull EntryType entryType,
		@BsonProperty("languageCode") @NonNull String languageCode
	) {
		this.id = id;
		this.snowflake = snowflake;
		this.entryType = entryType;
		this.languageCode = languageCode;
	}

	/**
	 * Constructs a new LocaleConfig entry.
	 *
	 * @param snowflake    the object's snowflake ID
	 * @param entryType    the type of entry
	 * @param languageCode the object's configured language code
	 */
	public LocaleConfig(long snowflake, @NonNull EntryType entryType, @NonNull String languageCode) {
		this.id = createId(entryType, snowflake);
		this.snowflake = snowflake;
		this.entryType = entryType;
		this.languageCode = languageCode;
	}

	/**
	 * Constructs a new LocaleConfig entry.
	 *
	 * @param snowflake the object's snowflake ID
	 * @param entryType the type of entry
	 * @param language  the object's configured language
	 */
	public LocaleConfig(long snowflake, @NonNull EntryType entryType, @NonNull Locale language) {
		this(snowflake, entryType, language.toLanguageTag());
	}

	/**
	 * Returns the database ID of this object.
	 *
	 * @return ID
	 */
	@NonNull
	@BsonId
	public String getId() {
		return id;
	}

	/**
	 * Returns the snowflake for this object.
	 *
	 * @return snowflake
	 */
	public long getSnowflake() {
		return snowflake;
	}

	/**
	 * Returns the type of this object.
	 *
	 * @return type
	 */
	public @NonNull EntryType getEntryType() {
		return entryType;
	}

	/**
	 * Returns the code of the configured language of this object.
	 *
	 * @return language code
	 */
	public @NonNull String getLanguageCode() {
		return languageCode;
	}

	/**
	 * A type of entry used to distinguish between snowflake IDs of users, channels, and guilds.
	 */
	public enum EntryType {

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
