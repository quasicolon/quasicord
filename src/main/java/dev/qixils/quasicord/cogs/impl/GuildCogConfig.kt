/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.cogs.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Getter
@NoArgsConstructor
public class GuildCogConfig {
	protected final boolean enabled = false;
}
