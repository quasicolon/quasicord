/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.converter.impl;

import dev.qixils.quasicord.Key;
import dev.qixils.quasicord.Quasicord;
import dev.qixils.quasicord.converter.Converter;
import dev.qixils.quasicord.error.UserError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.Interaction;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
public class DurationConverter implements Converter<String, Duration> {
	private final @NonNull Class<? extends Interaction> interactionClass = Interaction.class;
	private final @NonNull Class<String> inputClass = String.class;
	private final @NonNull Class<Duration> outputClass = Duration.class;
	private final @NonNull Quasicord library;

	private static final @NonNull Set<String> RELATIVE_TIME_IGNORED_TOKENS = Set.of("in");
	private static final @NonNull Pattern RELATIVE_TIME_PATTERN = Pattern.compile("^\\d+[A-Za-z]+");

	@Override
	public @NonNull Duration convert(@NonNull Interaction interaction, @NonNull String input) {
		List<String> arguments = new ArrayList<>(Arrays.asList(input.split(" ")));
		arguments.removeIf(s -> RELATIVE_TIME_IGNORED_TOKENS.contains(s.toLowerCase(Locale.ENGLISH)));
		String strippedInput = String.join("", arguments);

		if (!RELATIVE_TIME_PATTERN.matcher(strippedInput).matches()) {
			throw new UserError(Key.library("exception.duration.regex"));
		}

		boolean negative = false;
		if (arguments.get(0).startsWith("-")) {
			arguments.set(0, arguments.get(0).substring(1));
			negative = true;
		}
		if (arguments.get(arguments.size() - 1).equalsIgnoreCase("ago")) {
			arguments.remove(arguments.size() - 1);
			negative = !negative;
		}

		// vars for parsing the duration
		Duration duration = Duration.ZERO;
		StringBuilder nextToken = new StringBuilder();
		boolean buildingAmount = true;
		String amountToken = null;
		String unitToken;
		// vars for parsing the terms/tokens used fom the arg list
		StringBuilder term = new StringBuilder();
		int termIdx = 0;
		int parsedTermIdx = 0;

		for (char chr : strippedInput.toCharArray()) {
			term.append(chr);
			if (term.toString().equals(arguments.get(termIdx))) {
				term = new StringBuilder();
				++termIdx;
			}
			if (Character.isWhitespace(chr)) {
				continue;
			}

			boolean isDigit = Character.isDigit(chr);
			if (isDigit && !buildingAmount) {
				// finished parsing unit of time (and duration of time)
				buildingAmount = true;
				unitToken = nextToken.toString();
				nextToken = new StringBuilder();
				// now attempt to find the corresponding RelativeTimeUnit
				RelativeTimeUnit unit = RelativeTimeUnit.of(unitToken);
				if (unit == null) {
					throw new UserError(Key.library("exception.duration.unit"), unitToken);
				}
				// add relative time to current time object
				duration = unit.addTimeTo(duration, Long.parseLong(amountToken)); // theoretically this shouldn't error
				parsedTermIdx = termIdx;
				// reset variables
				amountToken = null;
			} else if (!isDigit && buildingAmount) {
				// finished parsing duration of time, save it & reset the builder
				buildingAmount = false;
				amountToken = nextToken.toString();
				nextToken = new StringBuilder();
			}
			nextToken.append(chr);
		}

		if (negative)
			duration = Duration.ZERO.minus(duration);

		while (parsedTermIdx > 0) {
			arguments.remove(0);
			parsedTermIdx--;
		}

		return duration;
	}

	private enum RelativeTimeUnit {
		SECOND(Duration::plusSeconds, "s", "sec", "secs", "second", "seconds"),
		MINUTE(Duration::plusMinutes, "m", "min", "mins", "minute", "minutes"),
		HOUR(Duration::plusHours, "h", "hr", "hrs", "hour", "hours"),
		DAY(Duration::plusDays, "d", "day", "days"),
		WEEK((duration, time) -> duration.plusDays(time * 7), "w", "week", "weeks");

		private final @NonNull BiFunction<@NonNull Duration, @NonNull Long, @NonNull Duration> addTimeFunction;
		private final String @NonNull [] tokens; // TODO: i18n plurals ?

		RelativeTimeUnit(
			@NonNull BiFunction<@NonNull Duration, @NonNull Long, @NonNull Duration> addTimeFunction,
			String @NonNull ... tokens
		) {
			this.addTimeFunction = addTimeFunction;
			this.tokens = tokens;
		}

		public boolean matches(@NonNull String input) {
			for (String token : tokens) {
				if (token.equalsIgnoreCase(input))
					return true;
			}
			return false;
		}

		public @NonNull Duration addTimeTo(@NonNull Duration originalTime, long duration) {
			return addTimeFunction.apply(originalTime, duration);
		}

		public static RelativeTimeUnit of(@NonNull String token) {
			for (RelativeTimeUnit unit : values()) {
				if (unit.matches(token))
					return unit;
			}
			return null;
		}
	}
}
