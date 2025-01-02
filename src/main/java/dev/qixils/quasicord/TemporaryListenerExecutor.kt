/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord

import dev.minn.jda.ktx.util.SLF4J
import dev.minn.jda.ktx.util.SLF4J.getValue
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class TemporaryListenerExecutor {
    private val listeners = ConcurrentHashMap<String, TemporaryListener<*>>()
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val logger by SLF4J

    /**
     * Registers a temporary listener.
     *
     * @param listener temporary listener to register
     */
    fun register(listener: TemporaryListener<*>) {
        val id = listener.id
        listeners[id] = listener
        executor.schedule(
            { listeners.remove(id) },
            listener.expiresAfter,
            TimeUnit.MILLISECONDS
        )
    }

    @SubscribeEvent
    suspend fun onEvent(event: GenericEvent) {
        val iterator = listeners.values.iterator()
        while (iterator.hasNext()) {
            val listener = iterator.next() as TemporaryListener<Any>
            val eventClass = listener.eventClass
            if (eventClass.isInstance(event)) {
                try {
                    if (!listener.predicate.invoke(event)) continue
					listener.callback.invoke(event)
                } catch (throwable: Throwable) {
                    logger.error("Temporary listener for '{}' threw an exception", eventClass.simpleName, throwable)
                }
                iterator.remove()
            }
        }
    }
}
