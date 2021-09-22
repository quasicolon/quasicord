package dev.qixils.quasicolon.locale;

import org.jetbrains.annotations.NotNull;

public final class LocaleManager {
	@NotNull
	public String localize(@NotNull String key, @NotNull Context context) {
		// TODO
	}

	@NotNull
	public String localize(@NotNull String key, @NotNull Context context, int amount) {
		// TODO
	}

	@NotNull
	public String localize(@NotNull Localizable localizable, @NotNull Context context) {
		return localize(localizable.getKey(), context);
	}

	@NotNull
	public String localize(@NotNull Localizable localizable, @NotNull Context context, int amount) {
		return localize(localizable.getKey(), context);
	}
}
