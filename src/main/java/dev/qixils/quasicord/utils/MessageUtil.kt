/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.utils

import com.google.errorprone.annotations.CheckReturnValue
import dev.qixils.quasicord.TemporaryListener
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.requests.ErrorResponse
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.function.Consumer

object MessageUtil {
    private val logger: Logger = LoggerFactory.getLogger(MessageUtil::class.java)

    fun setupComponentMenu(userId: Long, message: MessageCreateAction) {
        Objects.requireNonNull<MessageCreateAction>(message, "message cannot be null")
        // TODO (GH#19): Implement this
    }

    // TODO deprecate reaction menus and switch to ActionRow/components
    // TODO support continuously editing a message with a new y/n prompt
    /**
     * Adds a collection of emojis (unicode or custom) to a message and returns a
     * [TemporaryListener.Builder] which is configured to listen for the provided user
     * reacting with one of the listed emojis.
     * <br></br>
     * You must set your own `callback` on the listener and may set your own `onTimeout` or `length`.
     *
     * @param userID  user whose reactions are being listened for
     * @param message message to react on
     * @param emojis  emojis to react with
     * @return a temporary listener builder
     */
    @CheckReturnValue
    fun setupReactionMenu(
        userID: Long,
        message: Message,
        emojis: Iterable<Emoji>
    ): TemporaryListener.Builder<MessageReactionAddEvent> {
        for (emoji in emojis) {
            message.addReaction(emoji).queue(null, { throwable ->
                if (throwable is ErrorResponseException && throwable.errorResponse == ErrorResponse.UNKNOWN_EMOJI)
					logger.warn("The emoji '{}' was unknown", emoji, throwable)
            })
        }

        val messageID = message.idLong
        return TemporaryListener.Builder(MessageReactionAddEvent::class.java)
            .predicate { event ->
				event.messageIdLong == messageID && event.userIdLong == userID && emojis.contains(event.emoji)
			}
			.length(Duration.ofMinutes(1))
    }

    /**
     * Adds `yes` and `no` reactions to a message and returns a [TemporaryListener]
     * which is configured to listen for the provided user reacting with one of the listed emojis.
     *
     *
     * This will fire the `callback` with a boolean value if an input is received, else it will fire
     * with `null` if the input times out.
     *
     * @param userID   user whose reactions are being listened for
     * @param message  message to react on
     * @param callback callback to execute once input is received or times out
     * @return a temporary listener
     */
    @CheckReturnValue
    fun setupYesNoReactionMenu(
        userID: Long,
        message: Message,
        callback: Consumer<Boolean?>
    ): TemporaryListener<MessageReactionAddEvent> {
        val channel: MessageChannel = message.channel
        val yes = ContextualEmoji.YES.getEmoji(channel)
        val no = ContextualEmoji.NO.getEmoji(channel)
        return setupReactionMenu(userID, message, listOf(yes, no))
            .callback { event -> callback.accept(event.emoji == yes) }
			.onTimeout { callback.accept(null) }
			.build()
    }
}
