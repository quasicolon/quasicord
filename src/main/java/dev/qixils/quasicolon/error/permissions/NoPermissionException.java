package dev.qixils.quasicolon.error.permissions;

import dev.qixils.quasicolon.error.LocalizedException;
import org.jetbrains.annotations.NotNull;

public class NoPermissionException extends LocalizedException {
	public NoPermissionException(@NotNull String key) {
		super(key);
	}
}
