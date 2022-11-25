package dev.qixils.quasicolon.utils;

import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.TemporaryListener;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class QuasiButtonRow {
	private final QuasiButton @NonNull [] buttons;
	private final @NonNull Set<String> componentIds;
	private final @NonNull TemporaryListener<ButtonInteractionEvent> listener;

	public QuasiButtonRow(QuasiButton @NonNull ... buttons) {
		this.buttons = buttons;
		this.componentIds = Arrays.stream(buttons)
				.filter(btn -> btn.button().getId() != null)
				.map(btn -> Objects.requireNonNull(btn.button().getId()))
				.collect(Collectors.toUnmodifiableSet());
		this.listener = new TemporaryListener.Builder<>(ButtonInteractionEvent.class)
				.predicate(this::predicate)
				.callback(this::onInteract)
				.onTimeout(this::onTimeout)
				.length(Duration.ofMinutes(7))
				.build();
	}

	boolean predicate(@NonNull ButtonInteractionEvent event) {
		return componentIds.contains(event.getComponentId());
	}

	void onInteract(@NonNull ButtonInteractionEvent event) {
		for (QuasiButton button : buttons) {
			if (event.getComponentId().equals(button.button().getId())) {
				button.onInteract(event);
				return;
			}
		}
	}

	public void register(@NonNull Quasicolon quasicolon) {
		quasicolon.register(listener);
	}
}
