/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl;

import dev.qixils.quasicolon.Quasicord;
import dev.qixils.quasicolon.cogs.Command;
import dev.qixils.quasicolon.cogs.GuildCog;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Optional;


/**
 * A basic implementation of a cog that applies to only one guild.
 * <p>
 * All associated commands are automatically registered upon construction.
 */
public abstract class AbstractGuildCog extends AbstractCog implements GuildCog {
	@NonNull
	protected final Guild guild;

	/**
	 * Attempt to instantiate a new guild cog for the given guild.
	 * Returns None when the guild does not exist.
	 * Throws any exceptions thrown by the constructor for the cog.
	 */
	@NonNull
	public static <T extends AbstractGuildCog> Optional<T> TryLoad(@NonNull Quasicord library, long guildId, Class<T> cogType) throws InvocationTargetException {
		var guild = library.getJDA().getGuildById(guildId);
		// check if guild exists
		if (guild == null) {
			library.getLogger().warn("Guild {} does not exist!", guildId);
			return Optional.empty();
		} else {

			try {
				return Optional.of(cogType.getDeclaredConstructor(Quasicord.class, Guild.class).newInstance(library, guild));
			} catch (InstantiationException | IllegalAccessException |
					 NoSuchMethodException e) {
				// cogType is restricted to implement AbstractGuildCog.
				// AbstractGuildCog has exactly one constructor, which takes these two types.
				// Therefore, this exception should never be thrown.
				throw new Error(e);
			}
		}
	}

	protected AbstractGuildCog(@NonNull Quasicord library, @NonNull Guild guild) {
		super(library);
		this.guild = guild;

		onLoad();

		// register commands
		Collection<Command<?>> commands = getCommands();
		guild
			.updateCommands()
			.addCommands(commands.stream().map(Command::getCommandData).toList())
			.queue();
	}

	@Override
	public void onLoad() {
	}

	protected abstract Collection<GatewayIntent> getRequiredIntents();
}
