package dev.qixils.semicolon.variables.parsers;

import dev.qixils.semicolon.Semicolon;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class StringParser extends VariableParser<String> {
	public StringParser(@NotNull Semicolon bot) {
		super(bot);
	}

	@Override
	public @Nullable String fromDatabase(@NotNull String value) {
		return value;
	}

	@Override
	public @NotNull String toDatabase(@NotNull String string) {
		return string;
	}

	@Override
	public @NotNull CompletableFuture<@Nullable String> parseText(@NotNull Message context, @NotNull String humanText) {
		return CompletableFuture.completedFuture(humanText);
	}
}
