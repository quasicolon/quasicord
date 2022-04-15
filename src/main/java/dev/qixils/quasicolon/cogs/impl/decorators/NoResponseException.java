/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl.decorators;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.error.LocalizedRuntimeException;
import dev.qixils.quasicolon.text.Text;

/**
 * Thrown when an {@code @AutoSend} command fails to produce a response to a message.
 */
public class NoResponseException extends LocalizedRuntimeException {

	/**
	 * Constructs a new {@link NoResponseException}.
	 */
	public NoResponseException() {
		super(Text.single(Key.library("exception.no_response")));
	}
}
