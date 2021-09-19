package dev.qixils.semicolon.variables.parsers.snowflakes;

import dev.qixils.semicolon.Semicolon;
import dev.qixils.semicolon.utils.CollectionUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UserParser extends SnowflakeParser<User> {
	public UserParser(Semicolon bot) {
		super(bot);
	}

	@Override
	public @Nullable User fromDatabase(@NotNull String value) {
		return bot.getJda().getUserById(value);
	}

	@Override
	public @NotNull CompletableFuture<User> parseText(@NotNull Message context, @NotNull String humanText) {
		CompletableFuture<User> future = super.parseText(context, humanText);
		if (future.isDone())
			return future;
		return future.completeAsync(() -> {
			String text = humanText;
			Matcher matcher = TAG_PATTERN.matcher(text);
			String username = matcher.group(1);
			String discrim = matcher.group(2);
			User user;
			if (username != null && discrim != null) {
				user = bot.getJda().getUserByTag(username, discrim);
				return user;
			} else if (text.startsWith("@"))
				text = text.substring(1);

			final String finalText = text;

			List<User> users = bot.getJda().getUsers();
			List<Long> attemptedUsers = new ArrayList<>();

			user = CollectionUtil.first(users, usr -> usr.getName().equals(finalText));
			if (ask(context, user, attemptedUsers)) return user;

			user = CollectionUtil.first(users, usr -> usr.getName().equalsIgnoreCase(finalText));
			if (ask(context, user, attemptedUsers)) return user;

			List<Member> members = context.getGuild().getMembers();

			Member member = CollectionUtil.first(members, mmbr -> {
				String nickname = mmbr.getNickname();
				return nickname != null && nickname.equals(finalText);
			});
			if (member != null && ask(context, member.getUser(), attemptedUsers)) return member.getUser();

			member = CollectionUtil.first(members, mmbr -> {
				String nickname = mmbr.getNickname();
				return nickname != null && nickname.equalsIgnoreCase(finalText);
			});
			if (member != null && ask(context, member.getUser(), attemptedUsers)) return member.getUser();

			user = CollectionUtil.first(users, usr -> usr.getName().toLowerCase().startsWith(finalText));
			if (ask(context, user, attemptedUsers)) return user;

			member = CollectionUtil.first(members, mmbr -> {
				String nickname = mmbr.getNickname();
				return nickname != null && nickname.toLowerCase().startsWith(finalText.toLowerCase());
			});
			if (member != null && ask(context, member.getUser(), attemptedUsers)) return member.getUser();

			return null;
		});
	}

	private static final Pattern TAG_PATTERN = Pattern.compile("@?.{2,32}#\\d{4}");
}
