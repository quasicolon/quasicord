package dev.qixils.quasicolon.locale.text;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

public class LiteralText implements Text {
	private final @NonNull String text;

	public LiteralText(@NonNull String text) {
		this.text = text;
	}

	@Override
	public @NonNull String asString(@NonNull Locale locale) {
		return text;
	}
}
