/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon;

import dev.qixils.quasicolon.locale.TranslationProvider;
import dev.qixils.quasicolon.locale.translation.PluralTranslation;
import dev.qixils.quasicolon.locale.translation.SingleTranslation;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.Locale;

/**
 * A representation of a translation key.
 * <p>
 * This includes the usual {@link #value() string value} as well as the
 * {@link #namespace() namespace} in which the translation is defined.
 * </p>
 * The namespace always corresponds to a registered
 * {@link dev.qixils.quasicolon.locale.TranslationProvider TranslationProvider}.
 * For convenience, this <!-- TODO: I've just discovered this incomplete sentence and I've no idea what it's meant to say. -->
 */
@EqualsAndHashCode
@ToString
public final class Key {
	private final @NonNull String namespace;
	private final @NonNull String value;

	/**
	 * Construct a new key.
	 * All parameters are case-insensitive.
	 *
	 * @param namespace your bot or library's namespace
	 * @param value     the translation key
	 */
	public Key(@NonNull String namespace, @NonNull String value) {
		this.namespace = namespace.toLowerCase(Locale.ROOT);
		this.value = value.toLowerCase(Locale.ROOT);
	}

	// getters

	/**
	 * Get the namespace of this key.
	 *
	 * @return string namespace
	 */
	public @NonNull String namespace() {
		return namespace;
	}

	/**
	 * Get the value of this key.
	 *
	 * @return string value
	 */
	public @NonNull String value() {
		return value;
	}

	// helpers

	/**
	 * Get the translation provider for this key.
	 *
	 * @return translation provider
	 * @throws IllegalStateException if no translation provider is registered for the
	 *                               stored {@link #namespace() namespace}
	 */
	public @NonNull TranslationProvider translationProvider() throws IllegalStateException {
		return TranslationProvider.getInstance(this);
	}

	/**
	 * Gets this key's single translation (i.e. non-plural) for the given locale.
	 *
	 * @param locale the locale to get the translation for
	 * @return the translation
	 * @throws IllegalArgumentException if the translation is not a single translation
	 * @throws IllegalStateException    if no translation provider is registered for the
	 *                                  stored {@link #namespace() namespace}
	 */
	public @NonNull SingleTranslation getSingle(@NonNull Locale locale) {
		return translationProvider().getSingle(value, locale);
	}

	/**
	 * Gets this key's plural translation for the given locale.
	 *
	 * @param locale the locale to get the translation for
	 * @return the translation
	 * @throws IllegalArgumentException if the translation is not a plural translation
	 * @throws IllegalStateException    if no translation provider is registered for the
	 *                                  stored {@link #namespace() namespace}
	 */
	public @NonNull PluralTranslation getPlural(@NonNull Locale locale) {
		return translationProvider().getPlural(value, locale);
	}

	// static constructors

	/**
	 * The namespace internally used by the quasicord library.
	 */
	@Internal
	public static final @NonNull String LIBRARY_NAMESPACE = "quasicord";

	/**
	 * Constructs a new internal library key from the given value.
	 *
	 * @param value the translation key
	 * @return a new {@link Key}
	 */
	@Internal
	public static @NonNull Key library(@NonNull String value) {
		return new Key(LIBRARY_NAMESPACE, value);
	}

	/**
	 * Constructs a new key from the given namespace and value.
	 * All parameters are case-insensitive.
	 *
	 * @param namespace the namespace
	 * @param value     the translation key
	 * @return a new {@link Key}
	 */
	public static @NonNull Key of(@NonNull String namespace, @NonNull String value) {
		return new Key(namespace, value);
	}
}
