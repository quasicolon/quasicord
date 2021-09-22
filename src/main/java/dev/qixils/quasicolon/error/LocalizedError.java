package dev.qixils.quasicolon.error;

import dev.qixils.quasicolon.locale.Context;
import dev.qixils.quasicolon.locale.LocaleManager;
import dev.qixils.quasicolon.locale.Localizable;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record LocalizedError(LocaleManager localeManager,
							 @Getter String key) implements Localizable {
	public LocalizedError(@NotNull LocaleManager localeManager, @NotNull String key) {
		this.localeManager = Objects.requireNonNull(localeManager, "localizer");
		this.key = Objects.requireNonNull(key, "key");
	}

	@Override
	public String getKey() {
		return key;
	}

	public String localize(Context context) {
		return localeManager.localize(this, context);
	}

	public String localize(Context context, int amount) {
		return localeManager.localize(this, context, amount);
	}

	@Override
	public String toString() {
		return localize(Context.EMPTY);
	}
}
