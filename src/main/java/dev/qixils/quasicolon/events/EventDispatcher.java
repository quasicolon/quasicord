package dev.qixils.quasicolon.events;

import dev.qixils.quasicolon.registry.Registry;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

/**
 * Handles the registration of event listeners and dispatching of events.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class EventDispatcher {
	private static final @NonNull Logger LOGGER = LoggerFactory.getLogger(EventDispatcher.class);
	private final @NonNull MultiValuedMap<Class<?>, Consumer> listeners = new ArrayListValuedHashMap<>();
	private final @NonNull MultiValuedMap<Class<? extends Registry<?>>, Consumer<? extends Registry<?>>> registryInitListeners = new ArrayListValuedHashMap<>();

	/**
	 * Registers a listener for the given event class.
	 * 
	 * @param eventClass the event class to register the listener for
	 * @param listener    the listener to register
	 * @param <T>        the type of the event
	 */
	public <T> void registerListener(@NonNull Class<T> eventClass, @NonNull Consumer<T> listener) {
		listeners.put(eventClass, listener);
	}

	/**
	 * Registers a listener for a registry initialization event.
	 * 
	 * @param registryClass the registry class to register the listener for
	 * @param listener       the listener to register
	 * @param <T>           the type of the registry
	 */
	public <T extends Registry<?>> void registerRegistryInitListener(@NonNull Class<T> registryClass, @NonNull Consumer<T> listener) {
		registryInitListeners.put(registryClass, listener);
	}

	/**
	 * Registers all event listeners in the given class.
	 * <p>
	 * An event listener is a public method annotated with {@link EventListener} and takes a
	 * single parameter of the event type.
	 * </p>
	 * Some custom event listeners, such as {@link RegistryInitListener}, are also supported.
	 * 
	 * @param eventListeners the object instance containing the event listeners
	 */
	public void registerListeners(@NonNull Object eventListeners) {
		for (Method method : eventListeners.getClass().getMethods()) {
			if (Modifier.isStatic(method.getModifiers())) continue;
			if (method.getParameterCount() != 1) continue;
			if (method.isAnnotationPresent(EventListener.class)) {
				listeners.put(method.getParameterTypes()[0], event -> {
					try {
						method.invoke(eventListeners, event);
					} catch (Exception e) {
						throw new RuntimeException("Failed to invoke event handler", e);
					}
				});
			} else if (method.isAnnotationPresent(RegistryInitListener.class)) {
				RegistryInitListener annotation = method.getAnnotation(RegistryInitListener.class);
				registryInitListeners.put(annotation.value(), registry -> {
					try {
						method.invoke(eventListeners, registry);
					} catch (Exception e) {
						throw new RuntimeException("Failed to invoke registry event handler", e);
					}
				});
			}
		}
	}

	/**
	 * Dispatches the given event to all registered listeners.
	 *
	 * @param event the event to dispatch
	 */
	public void dispatch(@NonNull Object event) {
		for (Consumer handler : listeners.get(event.getClass())) {
			try {
				handler.accept(event);
			} catch (Exception e) {
				LOGGER.error("Error while dispatching event", e);
			}
		}
	}

	/**
	 * Dispatches the given registry initialization event to all registered listeners.
	 *
	 * @param registry the registry being initialized
	 */
	public void dispatchRegistryInit(@NonNull Registry<?> registry) {
		for (Consumer handler : registryInitListeners.get((Class<? extends Registry<?>>) registry.getClass())) {
			try {
				handler.accept(registry);
			} catch (Exception e) {
				LOGGER.error("Error while dispatching registry init event", e);
			}
		}
	}
}
