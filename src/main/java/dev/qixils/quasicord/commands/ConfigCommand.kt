/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.commands

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import dev.qixils.quasicord.Quasicord
import dev.qixils.quasicord.db.collection.LocaleConfig
import net.dv8tion.jda.api.entities.ISnowflake
import java.util.*

abstract class ConfigCommand protected constructor(@JvmField protected val library: Quasicord) {
    protected suspend fun setLocale(
        locale: Locale?,
        entryType: LocaleConfig.EntryType,
        snowflake: ISnowflake
    ) {
        // TODO: handle diacritics
        // TODO: probably remove per-channel locale config i think

        val collection = library.databaseManager.collection<LocaleConfig>()
        val filter = Filters.and(
            Filters.eq("entryType", entryType),
            Filters.eq("snowflake", snowflake.idLong),
        )
        if (locale == null) {
            collection.deleteOne(filter)
        } else {
            collection.replaceOne(
                filter,
                LocaleConfig(snowflake.idLong, entryType, locale),
                ReplaceOptions().upsert(true)
            )
        }
    }
}
