package dev.qixils.quasicolon.locale.translation;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A loaded translation with no plural forms.
 */
public interface SingleTranslation extends Translation {

	/**
	 * Gets the translated string.
	 *
	 * @return translated string
	 */
	@NonNull String get();
}
