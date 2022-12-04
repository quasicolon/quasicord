/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.variables;

import dev.qixils.quasicolon.Quasicord;
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
	public VariableParser<?> getVariableParser(@NonNull Quasicord bot) {
		return bot.getRootRegistry().VARIABLE_REGISTRY.get(name)
				.orElseThrow(() -> new IllegalStateException("VariableParser could not be found"));
	}
}
