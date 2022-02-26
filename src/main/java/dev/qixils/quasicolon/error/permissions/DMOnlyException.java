package dev.qixils.quasicolon.error.permissions;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.text.Text;

public class DMOnlyException extends NoPermissionException {
	public DMOnlyException() {
		super(Text.single(Key.library("exception.dms_only")));
	}
}
