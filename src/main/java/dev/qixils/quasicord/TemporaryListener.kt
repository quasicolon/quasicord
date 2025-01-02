/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord

import com.google.errorprone.annotations.CheckReturnValue
import de.huxhorn.sulky.ulid.ULID
import net.dv8tion.jda.api.events.GenericEvent
import org.checkerframework.common.value.qual.IntRange
import org.jetbrains.annotations.Contract
import java.time.Duration
import java.util.*
import kotlin.Throws

/**
 * A temporary listener for Discord events. The first matching event (defined by the [predicate][.getPredicate]
 * returning true) will be passed into the [callback][.getCallback], and then the listener will be discarded.
 *
 * @param <T> event type that will be listened for
</T> */
class TemporaryListener<T : GenericEvent?> @CheckReturnValue
/**
 * Constructs a temporary listener which listens for the first event specified by the `eventClass`, checks it
 * against the optional `predicate`, then calls the `callback` if the predicate passes.
 *
 * @param eventClass class of event that this object is listening for
 * @param callback   method that will be called with the event
 * @param length     how long the temporary listener will exist until it is discarded, in milliseconds
 * @param predicate  optional predicate which the event must pass
 */constructor(
	/**
	 * Gets the event class being listening for.
	 *
	 * @return event class
	 */
	val eventClass: Class<T>,
	/**
	 * Gets the milliseconds until the listener expires.
	 *
	 * @return expiry time
	 */
	val expiresAfter: Long,
	/**
	 * Gets the callback which will be executed once upon a matching event being found.
	 *
	 * @return event consumer callback
	 */
	val callback: suspend (T) -> Unit,
	predicate: (suspend (T) -> Boolean)? = null,
	onTimeout: (suspend () -> Unit)? = null
) {

	/**
     * Gets the predicate which must return `true` for an event to be considered matching.
     *
     * @return predicate
     */
    val predicate: suspend (T) -> Boolean = predicate ?: { true }

	/**
     * Gets the function which will be executed if the listener times out.
     *
     * @return timeout callback
     */
    val timeoutCallback: suspend () -> Unit = onTimeout ?: { }

    /**
     * Gets the unique identifier for this temporary listener.
     *
     * @return unique identifier
     */
    val id: String = ULID.nextULID()

    /**
     * Creates a new builder representing this temporary listener.
     *
     * @return new builder
     */
    @CheckReturnValue
    fun toBuilder(): Builder<T> {
        return Builder<T>()
			.eventClass(eventClass)
			.predicate(predicate)
			.callback(callback)
			.onTimeout(timeoutCallback)
			.length(expiresAfter)
    }

    /**
     * Registers this temporary listener.
     *
     * @param bot bot to register for
     */
    fun register(bot: Quasicord) {
		bot.register(this)
    }

    /**
     * Builder for [TemporaryListener].
     *
     * @param <T> event to listen for
    </T> */
    class Builder<T : GenericEvent?> {
        private var eventClass: Class<T>? = null
		private var predicate: (suspend (T) -> Boolean)? = null
		private var callback: (suspend (T) -> Unit)? = null
		private var onTimeout: (suspend () -> Unit)? = null
        private var length: Long = 0

        constructor()

        constructor(eventClass: Class<T>) {
            this.eventClass = eventClass
        }

        /**
         * Sets the event to listen for.
         *
         * @param eventClass class of the event to listen for
         * @return this builder
         */
        @Contract(value = "_ -> this", mutates = "this")
        fun eventClass(eventClass: Class<T>): Builder<T> {
            this.eventClass = eventClass
            return this
        }

        /**
         * Sets the filter which an event must pass (return `true`) for the [callback][.callback]
         * to be called.
         *
         * @param predicate event filter
         * @return this builder
         */
        @Contract(value = "_ -> this", mutates = "this")
        fun predicate(predicate: suspend (T) -> Boolean): Builder<T> {
            this.predicate = predicate
            return this
        }

        /**
         * Sets the callback which will be executed only once when a matching event is received.
         * A "matching event" is defined by the event being the same type as the [eventClass][.eventClass]
         * and passing the [predicate][.predicate].
         *
         * @param callback callback to execute
         * @return this builder
         */
        @Contract(value = "_ -> this", mutates = "this")
        fun callback(callback: suspend (T) -> Unit): Builder<T> {
            this.callback = callback
            return this
        }

        /**
         * Sets the method which will be run if the temporary listener expires without receiving an event.
         *
         * @param onTimeout method to execute on timeout
         * @return this builder
         */
        @Contract(value = "_ -> this", mutates = "this")
        fun onTimeout(onTimeout: suspend () -> Unit): Builder<T> {
            this.onTimeout = onTimeout
            return this
        }

        /**
         * Sets how many milliseconds until the temporary listener expires.
         *
         * @param length time in milliseconds
         * @return this builder
         */
        @Contract(value = "_ -> this", mutates = "this")
        fun length(length: @IntRange(from = 1L) Long): Builder<T> {
            this.length = length
            return this
        }

        /**
         * Sets the duration until the temporary listener expires.
         *
         * @param length duration
         * @return this builder
         */
        @Contract(value = "_ -> this", mutates = "this")
        fun length(length: Duration): Builder<T> {
            return length(Objects.requireNonNull(length, "length cannot be null").toMillis())
        }

        /**
         * Creates a new [TemporaryListener].
         *
         * @return the built temporary listener
         * @throws IllegalStateException a required parameter was not set
         */
        @CheckReturnValue
        @Throws(IllegalStateException::class)
        fun build(): TemporaryListener<T> {
			checkNotNull(eventClass) { "eventClass must be set" }
            checkNotNull(callback) { "callback must be set" }
            check(length > 0) { "length must be positive" }
            return TemporaryListener(eventClass!!, length, callback!!, predicate, onTimeout)
        }
    }

    companion object {
        private val ULID = ULID()
    }
}
