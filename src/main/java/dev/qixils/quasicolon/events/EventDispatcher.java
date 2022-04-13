package dev.qixils.quasicolon.events;

import dev.qixils.quasicolon.registry.Registry;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
public final class EventDispatcher {
	private static final @NonNull Logger LOGGER = LoggerFactory.getLogger(EventDispatcher.class);
	private final @NonNull MultiValuedMap<Class<?>, Consumer> handlers = new ArrayListValuedHashMap<>();
	private final @NonNull MultiValuedMap<Class<?>, Consumer<Registry<?>>> registryInitHandlers = new ArrayListValuedHashMap<>();

	public <T> void registerListener(@NonNull Class<T> eventClass, @NonNull Consumer<T> handler) {
		handlers.put(eventClass, handler);
	}

	public <T> void registerRegistryInitListener(@NonNull Class<T> eventClass, @NonNull Consumer<Registry<?>> handler) {
		registryInitHandlers.put(eventClass, handler);
	}

	public <T> void registerListeners(@NonNull EventHandler eventHandler) {
		for (Method method : eventHandler.getClass().getMethods()) {
			if (method.getParameterCount() != 1) continue;
			if (method.isAnnotationPresent(EventListener.class)) {
				handlers.put(method.getParameterTypes()[0], event -> {
					try {
						method.invoke(eventHandler, event);
					} catch (Exception e) {
						throw new RuntimeException("Failed to invoke event handler", e);
					}
				});
			} else if (method.isAnnotationPresent(RegistryInitListener.class)) {
				RegistryInitListener annotation = method.getAnnotation(RegistryInitListener.class);
				registryInitHandlers.put(annotation.value(), registry -> {
					try {
						method.invoke(eventHandler, registry);
					} catch (Exception e) {
						throw new RuntimeException("Failed to invoke registry event handler", e);
					}
				});
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> void dispatch(@NonNull T event) {
		for (Consumer handler : handlers.get(event.getClass())) {
			try {
				handler.accept(event);
			} catch (Exception e) {
				LOGGER.error("Error while dispatching event", e);
			}
		}
	}

	public void dispatchRegistryInit(@NonNull Registry<?> registry) {
		for (Consumer<Registry<?>> handler : registryInitHandlers.get(registry.getClass())) {
			try {
				handler.accept(registry);
			} catch (Exception e) {
				LOGGER.error("Error while dispatching registry init event", e);
			}
		}
	}
}
