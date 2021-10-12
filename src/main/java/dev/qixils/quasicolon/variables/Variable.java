package dev.qixils.quasicolon.variables;

import dev.qixils.quasicolon.QuasicolonBot;
import dev.qixils.quasicolon.variables.parsers.VariableParser;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Data
public class Variable {
	@BsonId
	private ObjectId _id;
	private long guildId; // guild ID
	private String data;
	private String name; // variable name (from Variables.java)

	@NotNull
	public VariableParser<?> getVariableParser(QuasicolonBot bot) {
		return getVariableParser(bot.getVariables());
	}

	@NotNull
	public VariableParser<?> getVariableParser(AbstractVariables variables) {
		return Objects.requireNonNull(variables.get(name), "VariableParser could not be found");
	}
}
