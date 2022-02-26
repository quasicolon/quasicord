package dev.qixils.quasicolon.error.permissions;

import dev.qixils.quasicolon.error.LocalizedException;
import dev.qixils.quasicolon.text.LocalizableText;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NoPermissionException extends LocalizedException {
	public NoPermissionException(@NonNull LocalizableText text) {
		super(text);
	}
}
