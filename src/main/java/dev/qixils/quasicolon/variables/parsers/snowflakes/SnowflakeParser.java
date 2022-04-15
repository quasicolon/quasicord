/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.variables.parsers.snowflakes;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.text.Text;
import dev.qixils.quasicolon.utils.FakeCollection;
import dev.qixils.quasicolon.utils.MessageUtil;
import dev.qixils.quasicolon.utils.PermissionUtil;
import dev.qixils.quasicolon.variables.parsers.VariableParser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.IPermissionContainer;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Contract;

import javax.annotation.CheckReturnValue;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public abstract class SnowflakeParser<R extends ISnowflake> extends VariableParser<R> {
	protected static final @NonNull Pattern SNOWFLAKE_PATTERN = Pattern.compile(".*(\\d{17,19})");
	private static final @NonNull Collection<Long> EMPTY_COLLECTION = new FakeCollection<>();

	public SnowflakeParser(@NonNull Quasicolon bot) {
		super(bot);
	}

	@Override
	public @NonNull String encode(@NonNull R snowflake) {
		return snowflake.getId();
	}

	@Override
	public @NonNull CompletableFuture<@Nullable R> parseText(@Nullable Message context, @NonNull String humanText) {
		return CompletableFuture.supplyAsync(() -> {
			String group = SNOWFLAKE_PATTERN.matcher(humanText).group(1);
			if (group != null) {
				try {
					return decode(group);
				} catch (Exception ignored) {
				}
			}
			return null;
		});
	}

	@Blocking
	@CheckReturnValue
	protected boolean ask(@NonNull Message context, @Nullable IMentionable mentionable) {
		return ask(context, mentionable, EMPTY_COLLECTION);
	}

	@Blocking
	@CheckReturnValue
	@Contract(mutates = "param3")
	protected boolean ask(@NonNull Message context, @Nullable IMentionable mentionable, @NonNull Collection<Long> attemptedObjects) {
		if (mentionable == null) return false;

		long id = mentionable.getIdLong();
		if (attemptedObjects.contains(id))
			return false;
		attemptedObjects.add(id);

		// ensure bot can speak and add reacts in the target channel
		MessageChannel channel = context.getChannel();
		if (channel instanceof IPermissionContainer &&
				!PermissionUtil.checkPermission((IPermissionContainer) channel, context.getGuild().getSelfMember(),
						Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ADD_REACTION))
			return false;

		// ask for confirmation
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		Text.single(Key.library("snowflake_confirm"))
				.sendAsReplyTo(context)
				.queue(reply -> MessageUtil.setupYesNoReactionMenu(context.getAuthor().getIdLong(), reply, input -> {
					reply.delete().queue();
					future.complete(input != null && input);
				}).register(bot));
		// .join() isn't ideal but all the usages of this method are inside async executors,
		// so it's alright
		return future.join();
	}
}
