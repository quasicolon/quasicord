package dev.qixils.quasicolon.variables.parsers.snowflakes;

import dev.qixils.quasicolon.QuasicolonBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class RoleParser extends SnowflakeParser<Role> {
	public RoleParser(QuasicolonBot bot) {
		super(bot);
	}

	@Override
	public @Nullable Role fromDatabase(@NotNull String value) {
		return bot.getJDA().getRoleById(value);
	}

	@Override
	public @NotNull CompletableFuture<@Nullable Role> parseText(@NotNull Message context, @NotNull String humanText) {
		return super.parseText(context, humanText).thenApply(superRole -> {
			if (superRole != null)
				return superRole;

			List<Role> roles = context.getGuild().getRoles();
			List<Long> attempted = new ArrayList<>();

			for (Role role : roles) {
				if (role.getName().equalsIgnoreCase(humanText) && ask(context, role, attempted))
					return role;
			}

			final String lowerText = humanText.toLowerCase();
			for (Role role : roles) {
				if (role.getName().toLowerCase().startsWith(lowerText) && ask(context, role, attempted))
					return role;
			}

			for (Role role : roles) {
				if (role.getName().toLowerCase().contains(lowerText) && ask(context, role, attempted))
					return role;
			}

			return null;
		});
	}
}
