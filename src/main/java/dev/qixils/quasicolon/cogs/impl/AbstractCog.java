/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl;

import dev.qixils.quasicolon.Quasicord;
import dev.qixils.quasicolon.cogs.ApplicationCommand;
import dev.qixils.quasicolon.cogs.Cog;
import dev.qixils.quasicolon.cogs.impl.decorators.AnnotationParser;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A basic implementation of a cog.
 * <p>
 * While extensions of this class are allowed, you should generally extend {@link AbstractGlobalCog}
 * or {@link AbstractGuildCog} instead.
 */
public abstract class AbstractCog implements Cog {

	protected final @NonNull Logger logger = LoggerFactory.getLogger(getClass());
	protected final @NonNull AnnotationParser parser;
	protected final @NonNull Quasicord library;
	private final @NonNull List<ApplicationCommand<?>> commands = new ArrayList<>();
	private boolean annotatedCommandsRegistered = false;

	protected AbstractCog(@NonNull Quasicord library) {
		this.library = library;
		this.parser = new AnnotationParser();
	}

	@NotNull
	@Override
	public Quasicord getLibrary() {
		return library;
	}

	@Override
	public @NonNull Collection<ApplicationCommand<?>> getCommands() {
		if (!annotatedCommandsRegistered) {
			annotatedCommandsRegistered = true;
			commands.addAll(parser.parse(this));
		}
		return commands;
	}

	protected void addCommand(@NonNull ApplicationCommand<?> commandData) {
		commands.add(commandData);
	}

	protected boolean areAnnotatedCommandsRegistered() {
		return annotatedCommandsRegistered;
	}
}
