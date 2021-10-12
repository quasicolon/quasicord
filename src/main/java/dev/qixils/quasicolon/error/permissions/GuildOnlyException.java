package dev.qixils.quasicolon.error.permissions;

public class GuildOnlyException extends NoPermissionException {
	public GuildOnlyException() {
		super("exception.guild_only");
	}
}
