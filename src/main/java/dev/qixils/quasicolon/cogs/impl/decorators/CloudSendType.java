package dev.qixils.quasicolon.cogs.impl.decorators;

/**
 * Which type of message to send for a command annotated with {@link CloudAutoSend}.
 */
public enum CloudSendType {
	/**
	 * Sends a generic (non-reply) message.
	 */
	DEFAULT,
	/**
	 * Sends a message that is a reply to the invoking author <b>without</b> a ping.
	 */
	REPLY,
	/**
	 * Sends a message that is a reply to the invoking author <b>with</b> a ping.
	 */
	PING_REPLY
}
