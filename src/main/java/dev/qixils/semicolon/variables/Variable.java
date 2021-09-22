package dev.qixils.semicolon.variables;

import dev.qixils.semicolon.Semicolon;
import dev.qixils.semicolon.variables.parsers.VariableParser;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Data
public class Variable {
	private long guildId; // guild ID
	private String data;
	private String name; // variable name (from Variables.java)

	@NotNull
	public VariableParser<?> getVariableParser(Semicolon bot) {
		return getVariableParser(bot.getVariables());
	}

	@NotNull
	public VariableParser<?> getVariableParser(Variables variables) {
		return Objects.requireNonNull(variables.get(name), "VariableParser could not be found");
	}
}
