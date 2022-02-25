package dev.qixils.quasicolon.locale.translation;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

/**
 * A translated object.
 */
public interface Translation {

	/**
	 * Gets the key of this translation.
	 *
	 * @return translation key
	 */
	@NonNull String getKey();

	/**
	 * Gets the locale of this translation.
	 *
	 * @return translation locale
	 */
	@NonNull Locale getLocale();

	/**
	 * Gets the originally requested locale for this translation.
	 * <p>
	 * This may be different from {@link #getLocale()} if the requested locale was unavailable or
	 * did not have a translation available for the requested key.
	 * </p>
	 *
	 * @return originally requested locale
	 */
	@NonNull Locale getRequestedLocale();
}
