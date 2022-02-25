package dev.qixils.quasicolon.locale.text;

import dev.qixils.quasicolon.locale.Localizable;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Text that can be localized into various languages.
 */
public interface LocalizableText extends Localizable, Text {

	/**
	 * Returns the formatting arguments that will be used to format the text.
	 *
	 * @return the formatting arguments
	 */
	String @NonNull [] getArgs();
}
