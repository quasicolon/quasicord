/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.commands;

import com.mongodb.client.model.ReplaceOptions;
import dev.qixils.quasicord.Quasicord;
import dev.qixils.quasicord.db.collection.LocaleConfig;
import net.dv8tion.jda.api.entities.ISnowflake;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.Locale;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public abstract class ConfigCommand {

	protected final @NonNull Quasicord library;

	protected ConfigCommand(@NonNull Quasicord library) {
		this.library = library;
	}

	protected Mono<?> setLocale(
		Locale locale,
		LocaleConfig.EntryType entryType,
		ISnowflake snowflake
	) {
		// TODO: handle diacritics
		// TODO: probably remove per-channel locale config i think

		var collection = library.getDatabaseManager().collection(LocaleConfig.class);
		var filter = and(eq("id", snowflake.getIdLong()), eq("entryType", entryType));
		Publisher<?> result;
		if (locale == null) {
			result = collection.deleteOne(filter);
		} else {
			result = collection.replaceOne(
				filter,
				new LocaleConfig(snowflake.getIdLong(), entryType, locale.toLanguageTag()),
				new ReplaceOptions().upsert(true)
			);
		}
		return Mono.from(result);
	}
}
