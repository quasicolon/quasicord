/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl.decorators.cloud;

import lombok.RequiredArgsConstructor;

/**
 * Which type of message to send for a command annotated with {@link CloudAutoSend}.
 */
@RequiredArgsConstructor
public enum CloudSendType {
	/**
	 * Sends a generic (non-reply) message.
	 */
	DEFAULT(false, false),
	/**
	 * Sends a message that is a reply to the invoking author <b>without</b> a ping.
	 */
	REPLY(true, false),
	/**
	 * Sends a message that is a reply to the invoking author <b>with</b> a ping.
	 */
	PING_REPLY(true, true);

	private final boolean isReply;
	private final boolean isPing;

	/**
	 * Returns whether this type represents a reply to a message.
	 *
	 * @return whether this type represents a reply to a message
	 */
	public boolean isReply() {
		return isReply;
	}

	/**
	 * Returns whether this type represents a message that will ping the invoking author.
	 *
	 * @return whether this type represents a message that will ping the invoking author
	 */
	public boolean isPing() {
		return isPing;
	}
}
