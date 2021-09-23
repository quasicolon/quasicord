package dev.qixils.quasicolon.variables.parsers.snowflakes;

import dev.qixils.quasicolon.QuasicolonBot;
import dev.qixils.quasicolon.utils.CollectionUtil;
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
	public UserParser(QuasicolonBot bot) {
		super(bot);
	}

	@Override
	public @Nullable User decode(@NotNull String value) {
		return bot.getJDA().getUserById(value);
	}

	@Override
	public @NotNull CompletableFuture<User> parseText(@Nullable Message context, @NotNull String humanText) {
		return super.parseText(context, humanText).thenApply(superUser -> {
			if (superUser != null)
				return superUser;

			String text = humanText;
			Matcher matcher = TAG_PATTERN.matcher(text);
			String username = matcher.group(1);
			String discrim = matcher.group(2);

			User user;
			if (username != null && discrim != null) {
				user = bot.getJDA().getUserByTag(username, discrim);
				return user;
			} else if (context == null) {
				return null;
			} else if (text.startsWith("@"))
				text = text.substring(1);

			final String finalText = text;

			List<User> users = bot.getJDA().getUsers();
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
