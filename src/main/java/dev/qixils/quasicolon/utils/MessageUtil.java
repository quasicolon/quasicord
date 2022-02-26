package dev.qixils.quasicolon.utils;

import dev.qixils.quasicolon.TemporaryListener;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@UtilityClass
public class MessageUtil {
	private static final @NonNull Logger logger = LoggerFactory.getLogger(MessageUtil.class);

	// TODO deprecate reaction menus and switch to ActionRow/components
	// also support continuously editing a message with a new y/n prompt

	/**
	 * Adds a collection of emotes (unicode or custom) to a message and returns a
	 * {@link TemporaryListener.Builder} which is configured to listen for the provided user
	 * reacting with one of the listed emotes.
	 * <br>
	 * You must set your own {@code callback} on the listener and may set your own {@code onTimeout} or {@code length}.
	 *
	 * @param userID  user whose reactions are being listened for
	 * @param message message to react on
	 * @param emotes  emotes to react with
	 * @return a temporary listener builder
	 */
	@CheckReturnValue
	public static TemporaryListener.@NonNull Builder<MessageReactionAddEvent> setupReactionMenu(long userID,
																								@NonNull Message message,
																								@NonNull Collection<String> emotes) {
		Objects.requireNonNull(message, "message cannot be null");
		Objects.requireNonNull(emotes, "emotes cannot be null");
		for (String emote : emotes) {
			if (emote == null || emote.isEmpty()) {
				logger.warn("Provided emote was null or empty", new IllegalArgumentException("Provided emote was null or empty"));
				continue;
			}

			message.addReaction(emote).queue($ -> {
			}, throwable -> {
				if (throwable instanceof ErrorResponseException error && error.getErrorResponse() == ErrorResponse.UNKNOWN_EMOJI)
					logger.warn("The emote '" + emote + "' was unknown", error);
			});
		}

		final long messageID = message.getIdLong();
		return new TemporaryListener.Builder<>(MessageReactionAddEvent.class)
				.predicate(event -> event.getMessageIdLong() == messageID &&
						event.getUserIdLong() == userID &&
						emotes.contains(EmoteUtil.asString(event.getReactionEmote())))
				.length(Duration.ofMinutes(1));
	}

	/**
	 * Adds {@code yes} and {@code no} reactions to a message and returns a {@link TemporaryListener}
	 * which is configured to listen for the provided user reacting with one of the listed emotes.
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
		final String yes = ContextualEmoji.YES.getEmojiString(channel);
		final String no = ContextualEmoji.NO.getEmojiString(channel);
		return setupReactionMenu(userID, message, List.of(yes, no))
				.callback(event -> callback.accept(EmoteUtil.asString(event.getReactionEmote()).equals(yes)))
				.onTimeout(() -> callback.accept(null))
				.build();
	}
}
