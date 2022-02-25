package dev.qixils.quasicolon.locale.impl;

import dev.qixils.quasicolon.locale.Context;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

@Accessors(fluent = true, chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class MutableContextImpl implements Context {
	private long user;
	private long channel;
	private long guild;

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(user, channel, guild);
	}

	public boolean equals(@Nullable Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Context other)) return false;
		if (user != other.user()) return false;
		if (channel != other.channel()) return false;
		return guild == other.guild();
	}
}
