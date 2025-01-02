/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.text

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.exceptions.RateLimitedException
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import net.dv8tion.jda.internal.requests.restaction.MessageCreateActionImpl
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Function
import kotlin.Throws

class TextMessageAction(channel: MessageChannel, private val text: Mono<String?>) : MessageCreateActionImpl(channel) {
    override fun addContent(content: String): MessageCreateAction {
        throw UnsupportedOperationException("Cannot add content to a TextMessageAction")
    }

    override fun setContent(content: String?): MessageCreateAction {
        throw UnsupportedOperationException("Cannot set content of a TextMessageAction")
    }

    // TODO: create wrapper class for the internal builder to also prevent setting content there?
    //       would need to find a workaround for the super.setContent(...) call below
    override fun queue(success: Consumer<in Message?>?, failure: Consumer<in Throwable?>?) {
        text.subscribe(
            Consumer { content: String? -> super.setContent(content) },
            failure,
            Runnable { super.queue(success, failure) })
    }

    override fun submit(shouldQueue: Boolean): CompletableFuture<Message?> {
        return text.toFuture().thenCompose<Message?>(Function { content: String? ->
            super.setContent(content)
            super.submit(shouldQueue)
        })
    }

    @Throws(RateLimitedException::class)
    override fun complete(shouldQueue: Boolean): Message {
        super.setContent(text.block())
        return super.complete(shouldQueue)
    }
}
