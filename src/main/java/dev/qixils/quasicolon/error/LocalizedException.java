package dev.qixils.quasicolon.error;

import dev.qixils.quasicolon.text.ForwardingLocalizedText;
import dev.qixils.quasicolon.text.LocalizableText;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LocalizedException extends Exception implements ForwardingLocalizedText {
	private final @Getter @NonNull LocalizableText text;

	public LocalizedException(@NonNull LocalizableText text) {
		this.text = text;
	}
}
