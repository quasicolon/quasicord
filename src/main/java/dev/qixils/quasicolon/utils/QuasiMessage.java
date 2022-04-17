package dev.qixils.quasicolon.utils;

import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.NonNull;

import dev.qixils.quasicolon.text.Text;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

/**
 * Object that stores {@link Text} to send and modifiers 
 * to the {@link MessageAction} created in sending the message.
 * 
 * This wrapper is useful for sending embeds or attachments.
 */
public record QuasiMessage(@NonNull Text text, @NonNull Consumer<MessageAction> modifier) { }
