package dev.qixils.quasicolon.cogs.impl;

import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.cogs.GlobalCog;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractGlobalCog extends AbstractCog implements GlobalCog {
	protected AbstractGlobalCog(@NonNull Quasicolon library) {
		super(library);
	}
}
