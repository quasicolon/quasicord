/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.converter.impl;

import dev.qixils.quasicord.Key;
import dev.qixils.quasicord.converter.Converter;
import dev.qixils.quasicord.error.UserError;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.interactions.Interaction;

import java.time.ZoneId;

/**
 * Attempts to find the timezone matching a user's input.
 * It does so by searching timezone IDs,
 * names of timezones in the user's detected language,
 * and names of timezones in English.
 */
@Getter
public class ZoneIdConverter implements Converter<String, ZoneId> {

	private final @NonNull Class<String> inputClass = String.class;
	private final @NonNull Class<ZoneId> outputClass = ZoneId.class;

	@Override
	public @NonNull ZoneId convert(@NonNull Interaction it, @NonNull String input) {
		try {
			return ZoneId.of(input);
		} catch (Exception ignored) {
			throw new UserError(Key.library("exception.invalid_timezone"), input);
		}
	}
}
