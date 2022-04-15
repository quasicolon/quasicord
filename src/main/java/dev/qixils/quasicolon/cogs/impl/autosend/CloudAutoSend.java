package dev.qixils.quasicolon.cogs.impl.autosend;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Cloud commands that return a {@link dev.qixils.quasicolon.text.Text} object which
 * should be sent as a Discord message.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CloudAutoSend {

	/**
	 * The type of message to send.
	 *
	 * @return message type
	 */
	@NonNull CloudSendType value();
}
