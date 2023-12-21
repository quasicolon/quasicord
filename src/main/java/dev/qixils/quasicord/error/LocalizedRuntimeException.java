/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.error;

import dev.qixils.quasicord.Key;
import dev.qixils.quasicord.text.ForwardingLocalizedText;
import dev.qixils.quasicord.text.LocalizableText;
import dev.qixils.quasicord.text.Text;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LocalizedRuntimeException extends RuntimeException implements ForwardingLocalizedText {
	private final @Getter @NonNull LocalizableText text;

	public LocalizedRuntimeException(@NonNull LocalizableText text) {
		this.text = text;
	}

	public LocalizedRuntimeException(@NonNull Key key, Object @NonNull ... args) {
		this(Text.single(key, args));
	}
}
