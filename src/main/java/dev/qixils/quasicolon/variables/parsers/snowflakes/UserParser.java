package dev.qixils.quasicolon.variables.parsers.snowflakes;

import dev.qixils.quasicolon.QuasicolonBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserParser extends SnowflakeParser<User> {
	private static final @NonNull Pattern TAG_PATTERN = Pattern.compile("@?.{2,32}#\\d{4}");

	public UserParser(@NonNull QuasicolonBot bot) {
		super(bot);
	}

	@Override
	public @Nullable User decode(@NonNull String value) {
		return bot.getJDA().getUserById(value);
	}

	@Override
	public @NonNull CompletableFuture<User> parseText(@Nullable Message context, @NonNull String humanText) {
		return super.parseText(context, humanText).thenApplyAsync(superUser -> {
			if (superUser != null)
				return superUser;

			String text = humanText;
			Matcher matcher = TAG_PATTERN.matcher(text);
			String username = matcher.group(1);
			String discrim = matcher.group(2);

			if (username != null && discrim != null)
				return bot.getJDA().getUserByTag(username, discrim);
			else if (context == null)
				return null;
			else if (text.startsWith("@"))
				text = text.substring(1);

			List<User> users = bot.getJDA().getUsers();
			List<Long> attemptedUsers = new ArrayList<>();

			for (User user : users) {
				if (user.getName().equals(text) && ask(context, user, attemptedUsers))
					return user;
			}

			for (User user : users) {
				if (user.getName().equalsIgnoreCase(text) && ask(context, user, attemptedUsers))
					return user;
			}

			List<Member> members = context.getGuild().getMembers();

			for (Member member : members) {
				String nickname = member.getNickname();
				if (nickname == null) continue;
				if (!nickname.equals(text)) continue;
				if (!ask(context, member, attemptedUsers)) continue;
				return member.getUser();
			}

			for (Member member : members) {
				String nickname = member.getNickname();
				if (nickname == null) continue;
				if (!nickname.equalsIgnoreCase(text)) continue;
				if (!ask(context, member, attemptedUsers)) continue;
				return member.getUser();
			}

			// TODO: use search algorithm from logbote?

			for (User user : users) {
				if (user.getName().toLowerCase().startsWith(text) && ask(context, user, attemptedUsers))
					return user;
			}

			for (Member member : members) {
				String nickname = member.getNickname();
				if (nickname == null) continue;
				if (!nickname.toLowerCase(Locale.ROOT).startsWith(text.toLowerCase(Locale.ROOT))) continue;
				if (!ask(context, member, attemptedUsers)) continue;
				return member.getUser();
			}

			return null;
		});
	}
}
