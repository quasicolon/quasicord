package dev.qixils.quasicolon.events;

import dev.qixils.quasicolon.registry.Registry;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is listening for the initialization of a specific registry.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RegistryInitListener {
	/**
	 * The type of registry to listen to.
	 *
	 * @return registry type
	 */
	@NonNull Class<? extends Registry<?>> value();
}
