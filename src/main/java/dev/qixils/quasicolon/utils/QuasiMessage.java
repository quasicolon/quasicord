package dev.qixils.quasicolon.utils;

import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.NonNull;

import dev.qixils.quasicolon.text.Text;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public record QuasiMessage(@NonNull Text text, @NonNull Consumer<MessageAction> modifier) {
}
