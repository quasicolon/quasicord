/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs;

/**
 * A {@link Cog} that applies to only one guild.
 * <p>
 * Guild cogs have no specific registration method. It is recommended that implementations of this
 * interface automatically register their associated commands upon construction.
 */
public interface GuildCog extends Cog {

}
