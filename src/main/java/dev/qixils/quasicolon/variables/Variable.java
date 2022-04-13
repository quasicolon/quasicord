package dev.qixils.quasicolon.variables;

import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.variables.parsers.VariableParser;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

@Data
public class Variable {
	@BsonId
	private ObjectId _id;
	private long guildId; // guild ID
	private String data;
	private String name; // variable name (from Variables.java)

	@NotNull
	public VariableParser<?> getVariableParser(@NonNull Quasicolon bot) {
		return bot.getRootRegistry().VARIABLE_REGISTRY.get(name)
				.orElseThrow(() -> new IllegalStateException("VariableParser could not be found"));
	}
}
