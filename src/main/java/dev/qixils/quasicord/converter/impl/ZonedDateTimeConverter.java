/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.converter.impl;

import dev.qixils.quasicord.Quasicord;
import dev.qixils.quasicord.converter.Converter;
import dev.qixils.quasicord.db.collection.TimeZoneConfig;
import dev.qixils.quasicord.error.UserError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.Interaction;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.qixils.quasicord.Key.library;

@Getter
@RequiredArgsConstructor
public class ZonedDateTimeConverter implements Converter<String, ZonedDateTime> {
	private final @NonNull Class<? extends Interaction> interactionClass = Interaction.class;
	private final @NonNull Class<String> inputClass = String.class;
	private final @NonNull Class<ZonedDateTime> outputClass = ZonedDateTime.class;
	private final @NonNull Quasicord library;

	private static final Pattern[] DATE_PATTERNS = {
			Pattern.compile("(?<year>\\d{4})-(?<month>\\d{1,2})-(?<day>\\d{1,2})"),
			Pattern.compile("(?<date1>\\d{1,2})/(?<date2>\\d{1,2})/(?<year>\\d{4})"),
			Pattern.compile("(?<month>\\p{L}{3,}) (?<day>\\d{1,2}) (?<year>\\d{4})", Pattern.UNICODE_CHARACTER_CLASS),
			Pattern.compile("(?<day>\\d{1,2}) (?<month>\\p{L}{3,}) (?<year>\\d{4})", Pattern.UNICODE_CHARACTER_CLASS),
	};
	private static final Pattern TIME_PATTERN = Pattern.compile("(?<hour>\\d{1,2})(?::(?<minute>\\d{2})(?::(?<second>\\d{2})(?:\\.(?<nanos>\\d{1,9}))?)?)?(?: ?(?<meridiem>[Aa]|[Pp])\\.?[Mm]\\.?)?");

	@Override
	public @NonNull ZonedDateTime convert(@NonNull Interaction interaction, @NonNull String input) {
		TimeZoneConfig config = library.getDatabaseManager().getById(interaction.getUser().getIdLong(), TimeZoneConfig.class).block();
		ZoneId zone = config == null ? ZoneOffset.UTC : config.getTimeZone();
		int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0, nanos = 0;
		char meridiem = 'x';

		// get date
		for (Pattern pattern : DATE_PATTERNS) {
			Matcher dateMatcher = pattern.matcher(input);
			if (!dateMatcher.find())
				continue;
			year = Integer.parseInt(dateMatcher.group("year"));
			if (dateMatcher.group("month") != null) {
				month = Integer.parseInt(dateMatcher.group("month"));
				day = Integer.parseInt(dateMatcher.group("day"));
			} else {
				// TODO: allow users to toggle between MM/DD and DD/MM in a /preference command
				month = Integer.parseInt(dateMatcher.group("date1"));
				day = Integer.parseInt(dateMatcher.group("date2"));
			}
		}

		// error if date is invalid
		if (month == 0 || day == 0 || month > 12 || day > 31)
			throw new UserError(library("exception.invalid_date"));

		// get time
		Matcher timeMatcher = TIME_PATTERN.matcher(input);
		if (timeMatcher.find()) {
			hour = Integer.parseInt(timeMatcher.group("hour"));
			minute = Integer.parseInt(timeMatcher.group("minute"));
			if (timeMatcher.group("second") != null)
				second = Integer.parseInt(timeMatcher.group("second"));
			if (timeMatcher.group("nanos") != null)
				// pad to 9 digits
				nanos = Integer.parseInt(timeMatcher.group("nanos") + "000000000".substring(timeMatcher.group("nanos").length()));
			if (timeMatcher.group("meridiem") != null)
				meridiem = Character.toLowerCase(timeMatcher.group("meridiem").charAt(0));
		}

		// error if time is invalid
		if (hour > 23 || minute > 59 || second > 59 || nanos > 999999999 || (meridiem != 'x' && hour > 12))
			throw new UserError(library("exception.invalid_time"));

		// handle meridiem
		if (meridiem == 'p' && hour != 12)
			hour += 12;
		else if (meridiem == 'a' && hour == 12)
			hour = 0;

		// return
		return ZonedDateTime.of(year, month, day, hour, minute, second, nanos, zone);
	}
}
