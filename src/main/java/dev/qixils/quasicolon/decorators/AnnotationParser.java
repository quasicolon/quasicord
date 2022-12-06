/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators;

import dev.qixils.quasicolon.cogs.Command;
import dev.qixils.quasicolon.decorators.slash.SlashCommand;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class AnnotationParser {

	public Collection<Command<?>> parse(Object object) {
		List<Command<?>> commands = new ArrayList<>();
		for (final Method method : object.getClass().getDeclaredMethods()) {
			ApplicationCommand applicationAnnotation = method.getAnnotation(ApplicationCommand.class);
			SlashCommand slashAnnotation = method.getAnnotation(SlashCommand.class);
			if (applicationAnnotation == null && slashAnnotation == null)
				continue;
			if (applicationAnnotation != null && slashAnnotation != null)
				throw new IllegalArgumentException("Cannot have both @ApplicationCommand and @SlashCommand on the same method");

			if (!method.canAccess(object))
				method.setAccessible(true);
			if (Modifier.isStatic(method.getModifiers()))
				throw new IllegalArgumentException("Decorator methods must not be static");

			if (applicationAnnotation != null)
				commands.add(parseApplicationCommand(object, method, applicationAnnotation));
			else
				commands.add(parseSlashCommand(object, method, slashAnnotation));
		}
		return commands;
	}

	private Command<?> parseApplicationCommand(Object object, Method method, ApplicationCommand annotation) {
		throw new UnsupportedOperationException("TODO");
	}

	private Command<?> parseSlashCommand(Object object, Method method, SlashCommand annotation) {
		throw new UnsupportedOperationException("TODO");
	}
}
