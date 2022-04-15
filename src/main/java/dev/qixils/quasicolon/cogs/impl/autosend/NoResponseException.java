package dev.qixils.quasicolon.cogs.impl.autosend;

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
