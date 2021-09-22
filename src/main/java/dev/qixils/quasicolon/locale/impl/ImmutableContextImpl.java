package dev.qixils.quasicolon.locale.impl;

import dev.qixils.quasicolon.locale.Context;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Accessors(fluent = true)
@Data
public record ImmutableContextImpl(long user, long channel, long guild) implements Context {

	@Override
	public @NotNull Context user(long user) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull Context channel(long channel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull Context guild(long guild) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		return Objects.hash(user, channel, guild);
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Context other)) return false;
		if (user != other.user()) return false;
		if (channel != other.channel()) return false;
		return guild == other.guild();
	}
}

