package dev.qixils.quasicolon.locale;

import dev.qixils.quasicolon.Key;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An object which possesses a translation key.
 */
public interface Localizable {

	/**
	 * Translation key corresponding to this object.
	 *
	 * @return translation key
	 */
	@NonNull Key getKey();
}
