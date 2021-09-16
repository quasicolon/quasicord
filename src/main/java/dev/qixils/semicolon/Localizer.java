package dev.qixils.semicolon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Localizer {

	@NotNull
	public String localize(@NotNull String key, @NotNull Context context) {
		// TODO
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	@Accessors(fluent = true, chain = true)
	public static class Context {
		private long user;
		private long channel;
		private long guild;

		// user

		public Context user(long user) {
			this.user = user;
			return this;
		}

		public Context user(User user) {
			return user(user.getIdLong());
		}

		public Context user(Member member) {
			return user(member.getIdLong());
		}

		// channel

		public Context channel(long channel) {
			this.channel = channel;
			return this;
		}

		public Context channel(TextChannel channel) {
			return channel(channel.getIdLong());
		}

		// guild

		public Context guild(long guild) {
			this.guild = guild;
			return this;
		}

		public Context guild(Guild guild) {
			return guild(guild.getIdLong());
		}

		// constructors

		public static Context fromMessage(Message message) {
			return new Context().user(message.getAuthor()).channel(message.getTextChannel()).guild(message.getGuild());
		}
	}
}
