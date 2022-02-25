package dev.qixils.quasicolon.variables;

import dev.qixils.quasicolon.variables.parsers.VariableParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Holds variables (settings that may be configured per-server) and their corresponding {@link VariableParser}.
 */
public abstract class AbstractVariables {
	/**
	 * The registry representing the map of variable names to their corresponding {@link VariableParser}.
	 */
	protected final Map<String, VariableParser<?>> REGISTRY = new HashMap<>();

	/**
	 * Registers a variable and its corresponding {@link VariableParser}.
	 *
	 * @param variable name of the variable to register
	 * @param parser   the variable's parser
	 * @return the variable's parser
	 * @throws NullPointerException     one or both of the input parameters was null
	 * @throws IllegalArgumentException a variable going by that name has already been registered
	 */
	@NotNull
	protected <T extends VariableParser<?>> T register(@NotNull String variable, @NotNull T parser) {
		Objects.requireNonNull(variable, "variable cannot be null");
		Objects.requireNonNull(parser, "parser cannot be null");

		if (REGISTRY.containsKey(variable))
			throw new IllegalArgumentException("Variable '" + variable + "' has already been registered");

		REGISTRY.put(variable, parser);
		return parser;
	}

	/**
	 * Gets the parser for a variable.
	 *
	 * @param variable name of a variable
	 * @return the parser, or null if the variable does not exist
	 * @throws NullPointerException the input parameter was null
	 */
	public @Nullable VariableParser<?> get(@NotNull String variable) {
		return REGISTRY.get(Objects.requireNonNull(variable, "variable cannot be null"));
	}
}
