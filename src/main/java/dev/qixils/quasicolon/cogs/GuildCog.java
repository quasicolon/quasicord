package dev.qixils.quasicolon.cogs;

import net.dv8tion.jda.api.entities.Guild;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

/**
 * A {@link Cog} that applies to only one guild.
 */
public interface GuildCog extends Cog {

	/**
	 * Gets the ID of the guild that this cog is for.
	 *
	 * @return guild ID
	 */
	long getGuildId();

	/**
	 * Gets the {@link Guild} that this cog is for.
	 *
	 * @return optional guild
	 */
	@NonNull
	default Optional<Guild> getGuild() {
		return Optional.ofNullable(getLibrary().getJDA().getGuildById(getGuildId()));
	}
}
