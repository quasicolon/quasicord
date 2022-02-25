package dev.qixils.quasicolon.text;


import dev.qixils.quasicolon.Key;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A class that simply forwards all calls to another {@link LocalizableText} instance.
 */
public interface ForwardingLocalizedText extends ForwardingText, LocalizableText {

	/**
	 * Returns the {@link LocalizableText} instance that this class forwards all calls to.
	 *
	 * @return delegated {@link LocalizableText} instance
	 */
	@Override
	@NonNull LocalizableText getText();

	@Override
	default @NonNull Key getKey() {
		return getText().getKey();
	}

	@Override
	default Object @NonNull [] getArgs() {
		return getText().getArgs();
	}
}
