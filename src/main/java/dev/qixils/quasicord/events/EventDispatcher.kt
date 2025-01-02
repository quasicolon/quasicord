/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.events

import dev.qixils.quasicord.registry.Registry
import org.apache.commons.collections4.MultiValuedMap
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import java.util.function.Consumer

/**
 * Handles the registration of event listeners and dispatching of events.
 */
class EventDispatcher {
    private val listeners: MultiValuedMap<Class<*>, Consumer<*>> = ArrayListValuedHashMap()
    private val registryInitListeners: MultiValuedMap<Class<out Registry<*>>, Consumer<out Registry<*>>> =
        ArrayListValuedHashMap()

    /**
     * Registers a listener for the given event class.
     *
     * @param eventClass the event class to register the listener for
     * @param listener    the listener to register
     * @param <T>        the type of the event
    </T> */
    fun <T> registerListener(eventClass: Class<T>, listener: Consumer<T>) {
        listeners.put(eventClass, listener)
    }

    /**
     * Registers a listener for a registry initialization event.
     *
     * @param registryClass the registry class to register the listener for
     * @param listener       the listener to register
     * @param <T>           the type of the registry
    </T> */
    fun <T : Registry<*>> registerRegistryInitListener(registryClass: Class<T>, listener: Consumer<T>) {
        registryInitListeners.put(registryClass, listener)
    }

    /**
     * Registers all event listeners in the given class.
     *
     *
     * An event listener is a public method annotated with [EventListener] and takes a
     * single parameter of the event type.
     *
     * Some custom event listeners, such as [RegistryInitListener], are also supported.
     *
     * @param eventListeners the object instance containing the event listeners
     */
    fun registerListeners(eventListeners: Any) {
        for (method in eventListeners.javaClass.methods) {
            if (Modifier.isStatic(method.modifiers)) continue
            if (method.parameterCount != 1) continue
            if (method.isAnnotationPresent(EventListener::class.java)) {
                listeners.put(method.parameterTypes[0]) { event ->
					try {
						// TODO: kotlin reflect
						method.invoke(eventListeners, event)
					} catch (e: Exception) {
						throw RuntimeException("Failed to invoke event handler", e)
					}
				}
			} else if (method.isAnnotationPresent(RegistryInitListener::class.java)) {
                val annotation = method.getAnnotation(RegistryInitListener::class.java)
                registryInitListeners.put(annotation.value.java) { registry ->
					try {
						method.invoke(eventListeners, registry)
					} catch (e: Exception) {
						throw RuntimeException("Failed to invoke registry event handler", e)
					}
				}
			}
        }
    }

    /**
     * Dispatches the given event to all registered listeners.
     *
     * @param event the event to dispatch
     */
    fun dispatch(event: Any) {
        for (handler in listeners.get(event.javaClass)) {
			handler as Consumer<Any> // generic
            try {
                handler.accept(event)
            } catch (e: Exception) {
                LOGGER.error("Error while dispatching event", e)
            }
        }
    }

    /**
     * Dispatches the given registry initialization event to all registered listeners.
     *
     * @param registry the registry being initialized
     */
    fun dispatchRegistryInit(registry: Registry<*>) {
        for (handler in registryInitListeners.get(registry.javaClass as Class<out Registry<*>?>)) {
			handler as Consumer<Any> // generic
            try {
                handler.accept(registry)
            } catch (e: Exception) {
                LOGGER.error("Error while dispatching registry init event", e)
            }
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(EventDispatcher::class.java)
    }
}
