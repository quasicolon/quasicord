/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.converter;

import net.dv8tion.jda.api.interactions.Interaction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An interface for converting an interaction to a different type.
 */
public interface VoidConverter<O> extends Converter<Void, O> {

	@Override
	default @NonNull O convert(@NonNull Interaction interaction, @Nullable Void input) {
		return convert(interaction);
	}

	/**
	 * Converts an interaction to the output type.
	 *
	 * @param interaction the interaction being invoked
	 * @return converted value
	 */
	@NonNull
	O convert(@NonNull Interaction interaction);
}
