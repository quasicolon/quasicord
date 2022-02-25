package dev.qixils.quasicolon.error;

import dev.qixils.quasicolon.text.ForwardingLocalizedText;
import dev.qixils.quasicolon.text.LocalizableText;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LocalizedRuntimeException extends RuntimeException implements ForwardingLocalizedText {
	private final @Getter @NonNull LocalizableText text;

	public LocalizedRuntimeException(@NonNull LocalizableText text) {
		this.text = text;
	}
}
