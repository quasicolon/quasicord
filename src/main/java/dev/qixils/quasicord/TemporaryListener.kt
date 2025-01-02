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
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.Throws

/**
 * A temporary listener for Discord events. The first matching event (defined by the [predicate][.getPredicate]
 * returning true) will be passed into the [callback][.getCallback], and then the listener will be discarded.
 *
 * @param <T> event type that will be listened for
</T> */
class TemporaryListener<T : GenericEvent?> @CheckReturnValue constructor(
    eventClass: Class<T>,
    predicate: Predicate<T>?,
    callback: Consumer<T>,
    onTimeout: Runnable?,
    private val length: Long
) {
    /**
     * Gets the event class being listening for.
     *
     * @return event class
     */
    @JvmField
	@get:CheckReturnValue
    val eventClass: Class<T>

    /**
     * Gets the predicate which must return `true` for an event to be considered matching.
     *
     * @return predicate
     */
    @JvmField
	@get:CheckReturnValue
    val predicate: Predicate<T>

    /**
     * Gets the callback which will be executed once upon a matching event being found.
     *
     * @return event consumer callback
     */
    @JvmField
	@get:CheckReturnValue
    val callback: Consumer<T>

    /**
     * Gets the function which will be executed if the listener times out.
     *
     * @return timeout callback
     */
    @get:CheckReturnValue
    val timeoutCallback: Runnable

    /**
     * Gets the unique identifier for this temporary listener.
     *
     * @return unique identifier
     */
    @get:CheckReturnValue
    val id: String = ULID.nextULID()

    /**
     * Constructs a temporary listener which listens for the first event specified by the `eventClass`, checks it
     * against the optional `predicate`, then calls the `callback` if the predicate passes.
     *
     * @param eventClass class of event that this object is listening for
     * @param callback   method that will be called with the event
     * @param length     how long the temporary listener will exist until it is discarded, in milliseconds
     * @param predicate  optional predicate which the event must pass
     */
    init {
        this.eventClass = Objects.requireNonNull(eventClass, "eventClass cannot be null")
        this.callback = Objects.requireNonNull(callback, "callback cannot be null")
        this.predicate = predicate ?: Predicate { true }
        this.timeoutCallback = onTimeout ?: Runnable {}
    }

    /**
     * Gets the milliseconds until the listener expires.
     *
     * @return expiry time
     */
    @CheckReturnValue
    fun expiresAfter(): Long {
        return length
    }

    /**
     * Creates a new builder representing this temporary listener.
     *
     * @return new builder
     */
    @CheckReturnValue
    fun toBuilder(): Builder<T> {
        return Builder<T>().eventClass(eventClass).predicate(predicate).callback(callback).onTimeout(
            timeoutCallback
        ).length(length)
    }

    /**
     * Registers this temporary listener.
     *
     * @param bot bot to register for
     */
    fun register(bot: Quasicord) {
        Objects.requireNonNull(bot, "bot cannot be null").register(this)
    }

    /**
     * Builder for [TemporaryListener].
     *
     * @param <T> event to listen for
    </T> */
    class Builder<T : GenericEvent?> {
        private var eventClass: Class<T>? = null
        private var predicate: Predicate<T>? = null
        private var callback: Consumer<T>? = null
        private var onTimeout: Runnable? = null
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
        fun predicate(predicate: Predicate<T>): Builder<T> {
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
        fun callback(callback: Consumer<T>): Builder<T> {
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
        fun onTimeout(onTimeout: Runnable): Builder<T> {
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
            return TemporaryListener(eventClass!!, predicate, callback!!, onTimeout, length)
        }
    }

    companion object {
        private val ULID = ULID()
    }
}
