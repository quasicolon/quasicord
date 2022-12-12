package dev.qixils.quasicolon.cogs.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Getter
@NoArgsConstructor
public class GuildCogConfig {
	protected final boolean enabled = false;
}
