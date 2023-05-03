/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs;

import dev.qixils.quasicolon.Quasicord;
import dev.qixils.quasicolon.registry.Registry;

/**
 * A {@link Cog} that applies to all guilds.
 * <p>
 * To register a global cog, call the
 * {@link dev.qixils.quasicolon.registry.Registry#register(Object) #register(GlobalCog)} method
 * during the {@link dev.qixils.quasicolon.registry.core.GlobalCogRegistry global cog registry}'s
 * {@link dev.qixils.quasicolon.events.EventDispatcher#dispatchRegistryInit(Registry) initialization
 * event} that is dispatched to your bot's
 * {@link Quasicord.Builder#eventHandler(Object) default event handler}. This will register all of this cog's
 * {@link #getCommands() commands}.
 */
public interface GlobalCog extends Cog {
}
