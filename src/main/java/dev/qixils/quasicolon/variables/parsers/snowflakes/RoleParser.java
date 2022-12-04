/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.variables.parsers.snowflakes;

import dev.qixils.quasicolon.Quasicord;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoleParser extends SnowflakeParser<Role> {
	public RoleParser(@NonNull Quasicord bot) {
		super(bot);
	}

	@Override
	public @Nullable Role decode(@NotNull String value) {
		return bot.getJDA().getRoleById(value);
	}

	@Override
	public @NotNull CompletableFuture<@Nullable Role> parseText(@Nullable Message context, @NotNull String humanText) {
		return super.parseText(context, humanText).thenApplyAsync(superRole -> {
			if (superRole != null || context == null)
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
