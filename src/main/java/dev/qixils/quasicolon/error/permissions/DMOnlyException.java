package dev.qixils.quasicolon.error.permissions;

public class DMOnlyException extends NoPermissionException {
	public DMOnlyException() {
		super("exception.dms_only");
	}
}
