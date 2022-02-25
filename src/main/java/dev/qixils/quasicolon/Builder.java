package dev.qixils.quasicolon;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An object which builds a certain immutable object.
 *
 * @param <R> the type of object being built
 */
public interface Builder<R> {

	/**
	 * Builds the object.
	 *
	 * @return the built object
	 * @throws IllegalStateException a required field is missing
	 */
	@NonNull R build() throws IllegalStateException;
}
