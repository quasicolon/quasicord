package dev.qixils.quasicolon;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
record QuasicordConfig(
		String token,
		Environment environment
) {
	public QuasicordConfig {
		if (environment == null) environment = Environment.TEST;
	}
}
