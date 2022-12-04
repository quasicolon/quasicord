/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl;

import dev.qixils.quasicolon.Quasicord;
import dev.qixils.quasicolon.cogs.GlobalCog;
import dev.qixils.quasicolon.registry.Registry;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A basic implementation of a cog that applies to all guilds.
 * <p>
 * To register a global cog, call the
 * {@link dev.qixils.quasicolon.registry.Registry#register(Object) #register(GlobalCog)} method
 * during the {@link dev.qixils.quasicolon.registry.core.GlobalCogRegistry global cog registry}'s
 * {@link dev.qixils.quasicolon.events.EventDispatcher#dispatchRegistryInit(Registry) initialization
 * event} that is dispatched to your bot's
 * {@link Quasicord.Builder#eventHandler(Object) default event handler}.
 */
public abstract class AbstractGlobalCog extends AbstractCog implements GlobalCog {
	protected AbstractGlobalCog(@NonNull Quasicord library) {
		super(library);
	}

	@Override
	public void onLoad() {
	}
}
