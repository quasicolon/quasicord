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

/**
 * An entry in the locale configuration database collection.
 * This stores the selected locale for a user, channel, or guild.
 */
@CollectionName(name = "locale")
public class LocaleConfig implements ISnowflake {
	private long id;
	private @Nullable EntryType entryType;
	private @Nullable String languageCode;

	/**
	 * No-arg constructor for MongoDB.
	 */
	LocaleConfig() {
	}

	/**
	 * Constructs a new LocaleConfig entry.
	 *
	 * @param id           the object's snowflake ID
	 * @param entryType    the type of entry
	 * @param languageCode the object's configured language code
	 */
	public LocaleConfig(long id, @NonNull EntryType entryType, @NonNull String languageCode) {
		this.id = id;
		this.entryType = entryType;
		this.languageCode = languageCode;
	}

	@Override
	public long getIdLong() {
		return id;
	}

	public @NonNull EntryType getEntryType() {
		if (entryType == null)
			throw new IllegalStateException("Entry type is null");
		return entryType;
	}

	public @NonNull String getLanguageCode() {
		if (languageCode == null)
			throw new IllegalStateException("Language code is null");
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
