package dev.qixils.quasicolon.error;

import dev.qixils.quasicolon.locale.Localizable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LocalizedException extends Exception implements Localizable {
	private final String key;

	public LocalizedException(@NotNull String key) {
		this.key = Objects.requireNonNull(key, "key");
	}

	@Override
	public @NotNull String getKey() {
		return key;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (LocalizedException) obj;
		return Objects.equals(this.key, that.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

}
