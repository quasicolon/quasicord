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

import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Optional;

/**
 * Attempts to find the Locale matching a user's input.
 * It does so by searching language tags,
 * names of languages in their detected language,
 * and names of languages in English.
 */
public class LocaleConverter implements Converter<String, Locale> {

	private final @NonNull @Getter Class<String> inputClass = String.class;
	private final @NonNull @Getter Class<Locale> outputClass = Locale.class;
	private final @NonNull Quasicord library;

	public LocaleConverter(@NonNull Quasicord library) {
		this.library = library;
	}

	@Override
	public @NonNull Locale convert(@NonNull Interaction it, @NonNull String input, @org.checkerframework.checker.nullness.qual.NonNull Class<? extends Locale> targetClass) {
		try {
			return new Locale.Builder().setLanguageTag(input).build();
		} catch (IllformedLocaleException ignored) {
			Locale userLocale = library.getLocaleProvider().forInteraction(it).block(); // TODO: async?? :(
			if (userLocale != null) {
				String localLowVal = input.toLowerCase(userLocale);
				Optional<Locale> localMatch = Locale.availableLocales()
					.filter(l -> l.getDisplayName(userLocale).toLowerCase(userLocale).equals(localLowVal))
					.findFirst();
				if (localMatch.isPresent())
					return localMatch.get();
			}

			String engLowVal = input.toLowerCase(Locale.ENGLISH);
			Optional<Locale> engMatch = Locale.availableLocales()
				.filter(l -> l.getDisplayName(Locale.ENGLISH).toLowerCase(Locale.ENGLISH).equals(engLowVal))
				.findFirst();
			if (engMatch.isPresent())
				return engMatch.get();

			throw new UserError(Key.library("exception.invalid_locale"), input);
		}
	}
}
