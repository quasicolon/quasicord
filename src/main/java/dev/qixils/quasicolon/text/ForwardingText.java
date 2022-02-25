package dev.qixils.quasicolon.text;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

/**
 * A class that simply forwards all calls to another {@link Text} instance.
 */
public interface ForwardingText extends Text {

	/**
	 * Returns the {@link Text} instance that this class forwards all calls to.
	 *
	 * @return delegated {@link Text} instance
	 */
	@NonNull Text getText();

	@Override
	default @NonNull String asString(@NonNull Locale locale) {
		return getText().asString(locale);
	}
}
