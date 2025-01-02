/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.converter;

import net.dv8tion.jda.api.interactions.Interaction;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

public class VoidConverterImpl<O> extends AbstractConverter<Void, O> implements VoidConverter<O> {

	private final @NonNull Function<Interaction, O> converter;

	public VoidConverterImpl(
			@NonNull Class<O> outputClass,
			@NonNull Function<Interaction, O> converter
	) {
		super(Void.class, outputClass);
		this.converter = converter;
	}

	@Override
	public @NonNull O convert(@NonNull Interaction interaction) {
		return converter.apply(interaction);
	}
}
