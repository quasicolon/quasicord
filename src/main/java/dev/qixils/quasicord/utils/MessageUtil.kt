/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.utils;

import com.google.errorprone.annotations.CheckReturnValue;
import dev.qixils.quasicord.TemporaryListener;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@UtilityClass
public class MessageUtil {
	private static final @NonNull Logger logger = LoggerFactory.getLogger(MessageUtil.class);

	public static void setupComponentMenu(long userId, @NonNull MessageCreateAction message) {
		Objects.requireNonNull(message, "message cannot be null");
		// TODO (GH#19): Implement this
	}

	// TODO deprecate reaction menus and switch to ActionRow/components
	// TODO support continuously editing a message with a new y/n prompt

	/**
	 * Adds a collection of emojis (unicode or custom) to a message and returns a
	 * {@link TemporaryListener.Builder} which is configured to listen for the provided user
	 * reacting with one of the listed emojis.
	 * <br>
	 * You must set your own {@code callback} on the listener and may set your own {@code onTimeout} or {@code length}.
	 *
	 * @param userID  user whose reactions are being listened for
	 * @param message message to react on
	 * @param emojis  emojis to react with
	 * @return a temporary listener builder
	 */
	@CheckReturnValue
	public static TemporaryListener.@NonNull Builder<MessageReactionAddEvent> setupReactionMenu(long userID,
																								@NonNull Message message,
																								@NonNull Collection<Emoji> emojis) {
		Objects.requireNonNull(message, "message cannot be null");
		Objects.requireNonNull(emojis, "emojis cannot be null");
		for (Emoji emoji : emojis) {
			if (emoji == null)
				throw new IllegalArgumentException("provided emoji was null");

			message.addReaction(emoji).queue(null, throwable -> {
				if (throwable instanceof ErrorResponseException error && error.getErrorResponse() == ErrorResponse.UNKNOWN_EMOJI)
					logger.warn("The emoji '" + emoji + "' was unknown", error);
			});
		}

		final long messageID = message.getIdLong();
		return new TemporaryListener.Builder<>(MessageReactionAddEvent.class)
				.predicate(event -> event.getMessageIdLong() == messageID &&
						event.getUserIdLong() == userID &&
						emojis.contains(event.getEmoji()))
				.length(Duration.ofMinutes(1));
	}

	/**
	 * Adds {@code yes} and {@code no} reactions to a message and returns a {@link TemporaryListener}
	 * which is configured to listen for the provided user reacting with one of the listed emojis.
	 * <p>
	 * This will fire the {@code callback} with a boolean value if an input is received, else it will fire
	 * with {@code null} if the input times out.
	 *
	 * @param userID   user whose reactions are being listened for
	 * @param message  message to react on
	 * @param callback callback to execute once input is received or times out
	 * @return a temporary listener
	 */
	@NonNull
	@CheckReturnValue
	public static TemporaryListener<MessageReactionAddEvent> setupYesNoReactionMenu(long userID, @NonNull Message message, @NonNull Consumer<Boolean> callback) {
		Objects.requireNonNull(message, "message cannot be null");
		Objects.requireNonNull(callback, "callback cannot be null");

		final MessageChannel channel = message.getChannel();
		final Emoji yes = ContextualEmoji.YES.getEmoji(channel);
		final Emoji no = ContextualEmoji.NO.getEmoji(channel);
		return setupReactionMenu(userID, message, List.of(yes, no))
				.callback(event -> callback.accept(event.getEmoji().equals(yes)))
				.onTimeout(() -> callback.accept(null))
				.build();
	}
}
