package dev.qixils.semicolon.locale;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface Context {
	long user();
	long channel();
	long guild();

	// user setter

	@NotNull Context user(long user);

	default @NotNull Context user(@NotNull User user) {
		return user(Objects.requireNonNull(user, "user").getIdLong());
	}

	default @NotNull Context user(@NotNull Member member) {
		return user(Objects.requireNonNull(member, "member").getIdLong());
	}

	// channel setter

	@NotNull Context channel(long channel);

	default @NotNull Context channel(@NotNull TextChannel channel) {
		return channel(Objects.requireNonNull(channel, "channel").getIdLong());
	}

	// guild setter

	@NotNull Context guild(long guild);

	default @NotNull Context guild(@NotNull Guild guild) {
		return guild(Objects.requireNonNull(guild, "guild").getIdLong());
	}

	// copy

	default @NotNull Context mutableCopy() {
		return new MutableContextImpl(user(), channel(), guild());
	}

	default @NotNull Context immutableCopy() {
		return new ImmutableContextImpl(user(), channel(), guild());
	}

	static Context fromMessage(Message message) {
		return new ImmutableContextImpl(message.getAuthor().getIdLong(), message.getTextChannel().getIdLong(), message.getGuild().getIdLong());
	}

	Context EMPTY = new ImmutableContextImpl();
}
