package dev.qixils.semicolon.variables;

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
	public VariableParser<?> getVariableParser() {
		return Objects.requireNonNull(Variables.get(name), "VariableParser could not be found");
	}
}
