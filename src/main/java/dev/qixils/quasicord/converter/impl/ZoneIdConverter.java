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
import lombok.NonNull;
import net.dv8tion.jda.api.interactions.Interaction;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

/**
 * Attempts to find the timezone matching a user's input.
 * It does so by searching timezone IDs,
 * names of timezones in the user's detected language,
 * and names of timezones in English.
 */
public class ZoneIdConverter implements Converter<String, ZoneId> {

	private final @NonNull @Getter Class<String> inputClass = String.class;
	private final @NonNull @Getter Class<ZoneId> outputClass = ZoneId.class;
	private final @NonNull Quasicord library;

	public ZoneIdConverter(@NonNull Quasicord library) {
		this.library = library;
	}

	@Override
	public @NonNull ZoneId convert(@NonNull Interaction it, @NonNull String input) {
		try {
			return ZoneId.of(input);
		} catch (Exception ignored) {
			Locale locale = library.getLocaleProvider().forInteraction(it).block(); // TODO: async?? :(

			if (locale != null) {
				String localLowVal = input.toLowerCase(locale);
				Optional<ZoneId> zoneMatch = ZoneId.getAvailableZoneIds()
					.stream()
					.map(ZoneId::of)
					.filter(id -> id.getDisplayName(TextStyle.FULL_STANDALONE, locale).toLowerCase(locale).equals(localLowVal))
					.findFirst();
				if (zoneMatch.isPresent())
					return zoneMatch.get();
			}

			String engLowVal = input.toLowerCase(Locale.ENGLISH);
			Optional<ZoneId> engMatch = ZoneId.getAvailableZoneIds()
				.stream()
				.map(ZoneId::of)
				.filter(l -> l.getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH).toLowerCase(Locale.ENGLISH).equals(engLowVal))
				.findFirst();
			if (engMatch.isPresent())
				return engMatch.get();

			throw new UserError(Key.library("exception.invalid_timezone"), input);
		}
	}
}
