/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.converter.impl

import dev.qixils.quasicord.Key.Companion.library
import dev.qixils.quasicord.Quasicord
import dev.qixils.quasicord.converter.Converter
import dev.qixils.quasicord.converter.impl.DurationConverter.RelativeTimeUnit.entries
import dev.qixils.quasicord.error.UserError
import net.dv8tion.jda.api.interactions.Interaction
import java.time.Duration
import java.util.regex.Pattern

class DurationConverter(private val library: Quasicord) : Converter<String, Duration> {
	override val inputClass: Class<String> = String::class.java
	override val outputClass: Class<Duration> = Duration::class.java

    override suspend fun convert(interaction: Interaction, input: String, targetClass: Class<out Duration?>): Duration {
        val arguments = input.split(" ")
			.dropLastWhile { it.isEmpty() }
			.filter { RELATIVE_TIME_IGNORED_TOKENS.contains(it.lowercase()) }
			.toMutableList()
        val strippedInput = arguments.joinToString("")

        if (!RELATIVE_TIME_PATTERN.matcher(strippedInput).matches()) {
            throw UserError(library("exception.duration.regex"))
        }

        var negative = false
        if (arguments.first().startsWith("-")) {
			arguments[0] = arguments.first().substring(1)
            negative = true
        }
        if (arguments.last().equals("ago", ignoreCase = true)) {
            arguments.removeLast()
            negative = !negative
        }

        // vars for parsing the duration
        var duration = Duration.ZERO
        var nextToken = StringBuilder()
        var buildingAmount = true
        var amountToken: String? = null
        var unitToken: String?
        // vars for parsing the terms/tokens used fom the arg list
        var term = StringBuilder()
        var termIdx = 0
        var parsedTermIdx = 0

        for (chr in strippedInput.toCharArray()) {
            term.append(chr)
            if (term.toString() == arguments[termIdx]) {
                term = StringBuilder()
                ++termIdx
            }
            if (Character.isWhitespace(chr)) {
                continue
            }

            val isDigit = Character.isDigit(chr)
            if (isDigit && !buildingAmount) {
                // finished parsing unit of time (and duration of time)
                buildingAmount = true
                unitToken = nextToken.toString()
                nextToken = StringBuilder()
                // now attempt to find the corresponding RelativeTimeUnit
                val unit = RelativeTimeUnit.Companion.of(unitToken)
                if (unit == null) {
                    throw UserError(library("exception.duration.unit"), unitToken)
                }
                // add relative time to current time object
                duration = unit.addTimeTo(duration, amountToken!!.toLong()) // theoretically this shouldn't error
                parsedTermIdx = termIdx
                // reset variables
                amountToken = null
            } else if (!isDigit && buildingAmount) {
                // finished parsing duration of time, save it & reset the builder
                buildingAmount = false
                amountToken = nextToken.toString()
                nextToken = StringBuilder()
            }
            nextToken.append(chr)
        }

        if (negative) duration = Duration.ZERO.minus(duration)

        while (parsedTermIdx > 0) {
            arguments.removeFirst()
            parsedTermIdx--
        }

        return duration
    }

    private enum class RelativeTimeUnit(
        private val addTimeFunction: (Duration, Long) -> Duration,
		private vararg val tokens: String
    ) {
        SECOND(
            { obj, secondsToAdd -> obj.plusSeconds(secondsToAdd) },
            "s",
            "sec",
            "secs",
            "second",
            "seconds"
        ),
        MINUTE(
            { obj, minutesToAdd -> obj.plusMinutes(minutesToAdd) },
            "m",
            "min",
            "mins",
            "minute",
            "minutes"
        ),
        HOUR(
            { obj, hoursToAdd -> obj.plusHours(hoursToAdd) },
            "h",
            "hr",
            "hrs",
            "hour",
            "hours"
        ),
        DAY({ obj, daysToAdd -> obj.plusDays(daysToAdd) }, "d", "day", "days"),
        WEEK({ duration, time -> duration.plusDays(time * 7) }, "w", "week", "weeks");

		fun matches(input: String): Boolean {
            for (token in tokens) {
                if (token.equals(input, ignoreCase = true)) return true
            }
            return false
        }

        fun addTimeTo(originalTime: Duration, duration: Long): Duration {
            return addTimeFunction.invoke(originalTime, duration)
        }

        companion object {
            fun of(token: String): RelativeTimeUnit? {
                for (unit in entries) {
                    if (unit.matches(token)) return unit
                }
                return null
            }
        }
    }

    companion object {
        private val RELATIVE_TIME_IGNORED_TOKENS = mutableSetOf<String?>("in")
        private val RELATIVE_TIME_PATTERN: Pattern = Pattern.compile("^\\d+[A-Za-z]+")
    }
}
