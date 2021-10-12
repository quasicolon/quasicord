package dev.qixils.quasicolon.error.permissions;

public class OwnerOnlyException extends NoPermissionException {
	public OwnerOnlyException() {
		super("exception.owner_only");
	}
}
