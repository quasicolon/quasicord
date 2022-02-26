package dev.qixils.quasicolon.error.permissions;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.text.Text;

public class OwnerOnlyException extends NoPermissionException {
	public OwnerOnlyException() {
		super(Text.single(Key.library("exception.owner_only")));
	}
}
