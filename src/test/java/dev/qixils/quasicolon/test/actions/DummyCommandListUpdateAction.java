/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.test.actions;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class DummyCommandListUpdateAction extends DummyRestAction<List<Command>> implements CommandListUpdateAction {

	public DummyCommandListUpdateAction(JDA jda) {
		super(jda, Collections.emptyList());
	}

	@NotNull
	@Override
	public CommandListUpdateAction timeout(long timeout, @NotNull TimeUnit unit) {
		return (CommandListUpdateAction) super.timeout(timeout, unit);
	}

	@NotNull
	@Override
	public CommandListUpdateAction deadline(long timestamp) {
		return (CommandListUpdateAction) super.deadline(timestamp);
	}

	@NotNull
	@Override
	public CommandListUpdateAction addCheck(@NotNull BooleanSupplier checks) {
		return (CommandListUpdateAction) super.addCheck(checks);
	}

	@Override
	public @NotNull CommandListUpdateAction setCheck(@Nullable BooleanSupplier checks) {
		return (CommandListUpdateAction) super.setCheck(checks);
	}

	@NotNull
	@Override
	public CommandListUpdateAction addCommands(@NotNull Collection<? extends CommandData> commands) {
		return this;
	}
}
