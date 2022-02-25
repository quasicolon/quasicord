package dev.qixils.quasicolon.text;

import dev.qixils.quasicolon.Key;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

abstract class AbstractLocalizableText implements LocalizableText {
	protected final @Getter @NonNull Key key;
	protected final @Getter Object @NonNull [] args;

	AbstractLocalizableText(@NonNull Key key, Object @Nullable [] args) {
		this.key = key;
		this.args = Objects.requireNonNullElseGet(args, () -> new Object[]{});
	}
}
