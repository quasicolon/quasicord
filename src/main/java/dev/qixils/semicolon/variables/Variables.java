package dev.qixils.semicolon.variables;

import dev.qixils.semicolon.Semicolon;
import dev.qixils.semicolon.variables.parsers.VariableParser;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public final class Variables {
	private final Map<String, VariableParser<?>> REGISTRY = new HashMap<>();

	@NotNull
	private <T extends VariableParser<?>> T register(@NotNull String variable, @NotNull T parser) {
		REGISTRY.put(Objects.requireNonNull(variable, "variable"),
					 Objects.requireNonNull(parser, "parser"));
		return parser;
	}

	@Nullable
	public VariableParser<?> get(@NotNull String variable) {
		return REGISTRY.get(Objects.requireNonNull(variable, "variable cannot be null"));
	}

	public static void initialize(@NotNull Semicolon bot) {
		Objects.requireNonNull(bot, "bot cannot be null");
		JDA jda = bot.getJda();
	}
}
