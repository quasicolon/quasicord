package dev.qixils.quasicolon.text;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

record LiteralText(@NonNull String text) implements Text {
	@Override
	public @NonNull String asString(@NonNull Locale locale) {
		return text;
	}
}
