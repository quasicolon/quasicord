package dev.qixils.semicolon.error;

import dev.qixils.semicolon.locale.Context;
import dev.qixils.semicolon.locale.Localizable;
import dev.qixils.semicolon.locale.Localizer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record LocalizedError(Localizer localizer,
							 @Getter String key) implements Localizable {
	public LocalizedError(@NotNull Localizer localizer, @NotNull String key) {
		this.localizer = Objects.requireNonNull(localizer, "localizer");
		this.key = Objects.requireNonNull(key, "key");
	}

	@Override
	public String getKey() {
		return key;
	}

	public String localize(Context context) {
		return localizer.localize(this, context);
	}

	public String localize(Context context, int amount) {
		return localizer.localize(this, context, amount);
	}

	@Override
	public String toString() {
		return localize(Context.EMPTY);
	}
}
