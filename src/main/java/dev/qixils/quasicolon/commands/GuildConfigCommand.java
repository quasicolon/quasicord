/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.commands;

import dev.qixils.quasicolon.decorators.Namespace;
import dev.qixils.quasicolon.decorators.slash.SlashCommand;

@Namespace("quasicord")
@SlashCommand(value = "guild-config", guildOnly = true)
public class GuildConfigCommand {
}
