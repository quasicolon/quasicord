package dev.qixils.quasicolon.utils;

import lombok.Data;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

@Data
@Accessors(fluent = true)
public final class QuasiButton {
	private final @NonNull Button button;
	private final @NonNull Consumer<ButtonInteractionEvent> onInteract;

	public void onInteract(@NonNull ButtonInteractionEvent event) {
		onInteract.accept(event);
	}
}
