package dev.qixils.quasicolon.cogs;

import dev.qixils.quasicolon.registry.Registry;

/**
 * A {@link Cog} that applies to all guilds.
 * <p>
 * To register a global cog, call the
 * {@link dev.qixils.quasicolon.registry.Registry#register(Object) #register(GlobalCog)} method
 * during the {@link dev.qixils.quasicolon.registry.core.GlobalCogRegistry global cog registry}'s
 * {@link dev.qixils.quasicolon.events.EventDispatcher#dispatchRegistryInit(Registry) initialization
 * event}.
 */
public interface GlobalCog extends Cog {
}
