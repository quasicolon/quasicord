package dev.qixils.quasicolon.cogs;

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

}
