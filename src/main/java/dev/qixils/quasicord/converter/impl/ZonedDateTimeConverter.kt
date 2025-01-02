/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.converter.impl

import dev.qixils.quasicord.Key.Companion.library
import dev.qixils.quasicord.Quasicord
import dev.qixils.quasicord.converter.Converter
import dev.qixils.quasicord.db.collection.TimeZoneConfig
import dev.qixils.quasicord.error.UserError
import net.dv8tion.jda.api.interactions.Interaction
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.regex.Matcher
import java.util.regex.Pattern

class ZonedDateTimeConverter(private val library: Quasicord) : Converter<String, ZonedDateTime> {
	override val inputClass: Class<String> = String::class.java
    override val outputClass: Class<ZonedDateTime> = ZonedDateTime::class.java
    private val durationConverter: DurationConverter = DurationConverter(library)

	override suspend fun convert(
        interaction: Interaction,
        input: String,
        targetClass: Class<out ZonedDateTime?>
    ): ZonedDateTime {
        val config = library.databaseManager.getById(
			"snowflake",
            interaction.user.idLong,
            TimeZoneConfig::class.java
        )
        val zone = config?.timeZone ?: ZoneOffset.UTC
        val now = ZonedDateTime.now(zone)

        try {
            val add = durationConverter.convert(interaction, input)
            return now.plus(add)
        } catch (_: Exception) {
        }

        var autoYear = true
        var year = now.year
        var month = 0
        var day = 0
        var hour = 0
        var minute = 0
        var second = 0
        var nanos = 0
        var meridiem = 'x'

        // get date
        for (pattern in DATE_PATTERNS) {
            val dateMatcher = pattern!!.matcher(input)
            if (!dateMatcher.find()) continue
            if (dateMatcher.group("year") != null) {
                year = dateMatcher.group("year").toInt()
                autoYear = false
            }
            if (dateMatcher.group("month") != null) {
                // TODO month name parse
                month = dateMatcher.group("month").toInt()
                day = dateMatcher.group("day").toInt()
            } else {
                // TODO: allow users to toggle between MM/DD and DD/MM in a /preference command (or just guess from locale)
                month = dateMatcher.group("date1").toInt()
                day = dateMatcher.group("date2").toInt()
            }
            break
        }

        // error if date is invalid
        val autoDate = month == 0 && day == 0
        if (autoDate) {
            year = now.year
            month = now.monthValue
            day = now.dayOfMonth
        } else if (month < 1 || day < 1 || year == 0 || month > 12 || day > 31) throw UserError(library("exception.invalid_date"))

        // get time
        val timeMatcher: Matcher = TIME_PATTERN.matcher(input)
        if (timeMatcher.find()) {
            hour = timeMatcher.group("hour").toInt()
            minute = timeMatcher.group("minute").toInt()
            if (timeMatcher.group("second") != null) second = timeMatcher.group("second").toInt()
            if (timeMatcher.group("nanos") != null)  // pad to 9 digits
                nanos = (timeMatcher.group("nanos") + "000000000".substring(timeMatcher.group("nanos").length)).toInt()
            if (timeMatcher.group("meridiem") != null) meridiem = timeMatcher.group("meridiem").get(0).lowercaseChar()
        }

        // error if time is invalid
        if (hour > 23 || minute > 59 || second > 59 || nanos > 999999999 || (meridiem != 'x' && hour > 12)) throw UserError(
            library("exception.invalid_time")
        )

        // handle meridiem
        if (meridiem == 'p' && hour != 12) hour += 12
        else if (meridiem == 'a' && hour == 12) hour = 0

        // return
        var zdt = ZonedDateTime.of(year, month, day, hour, minute, second, nanos, zone)
        if (zdt.isBefore(now)) {
            if (autoYear) zdt = ZonedDateTime.of(year + 1, month, day, hour, minute, second, nanos, zone)
            else if (autoDate) zdt =
                zdt.plusDays(1) // TODO: this might pose accuracy issues around daylight savings crossovers? idfk
        }
        return zdt
    }

    companion object {
        private val DATE_PATTERNS = arrayOf<Pattern?>(
            Pattern.compile("(?<year>\\d{4})-(?<month>\\d{1,2})-(?<day>\\d{1,2})"),
            Pattern.compile("(?<date1>\\d{1,2})/(?<date2>\\d{1,2})(?:/(?<year>\\d{4}))?"),  // TODO month name parse
            //Pattern.compile("(?<month>\\p{L}{3,}) (?<day>\\d{1,2}) (?<year>\\d{4})", Pattern.UNICODE_CHARACTER_CLASS),
            //Pattern.compile("(?<day>\\d{1,2}) (?<month>\\p{L}{3,}) (?<year>\\d{4})", Pattern.UNICODE_CHARACTER_CLASS),
        )
        private val TIME_PATTERN: Pattern =
            Pattern.compile("(?<hour>\\d{1,2})(?::(?<minute>\\d{2})(?::(?<second>\\d{2})(?:\\.(?<nanos>\\d{1,9}))?)?)?(?: ?(?<meridiem>[Aa]|[Pp])\\.?[Mm]\\.?)?")
    }
}
