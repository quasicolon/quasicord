package dev.qixils.quasicolon.error.permissions;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.text.Text;

public class GuildOnlyException extends NoPermissionException {
	public GuildOnlyException() {
		super(Text.single(Key.library("exception.guild_only")));
	}
}
