/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.error;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.text.ForwardingLocalizedText;
import dev.qixils.quasicolon.text.LocalizableText;
import dev.qixils.quasicolon.text.Text;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LocalizedException extends Exception implements ForwardingLocalizedText {
	private final @Getter @NonNull LocalizableText text;

	public LocalizedException(@NonNull LocalizableText text) {
		this.text = text;
	}

	public LocalizedException(@NonNull Key key, Object @NonNull ... args) {
		this(Text.single(key, args));
	}
}
