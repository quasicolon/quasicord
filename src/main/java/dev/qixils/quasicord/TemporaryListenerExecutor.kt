/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord

import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Predicate

class TemporaryListenerExecutor {
    private val listeners = ConcurrentHashMap<String, TemporaryListener<*>>()
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Registers a temporary listener.
     *
     * @param listener temporary listener to register
     */
    fun register(listener: TemporaryListener<*>) {
        Objects.requireNonNull(listener, "listener cannot be null")

        val id = listener.id
        listeners[id] = listener
        executor.schedule(
            Runnable { listeners.remove(id) },
            listener.expiresAfter(),
            TimeUnit.MILLISECONDS
        )
    }

    @SubscribeEvent
    fun onEvent(event: GenericEvent) {
        val iterator = listeners.values.iterator()
        while (iterator.hasNext()) {
            val listener = iterator.next()
            val eventClass: Class<*> = listener.eventClass
            if (eventClass.isInstance(event)) {
                try {
                    if (!(listener.predicate as Predicate<GenericEvent?>).test(event)) continue
                    (listener.callback as Consumer<GenericEvent?>).accept(event)
                } catch (throwable: Throwable) {
                    logger.error("Temporary listener for '" + eventClass.simpleName + "' threw an exception", throwable)
                }
                iterator.remove()
            }
        }
    }
}
