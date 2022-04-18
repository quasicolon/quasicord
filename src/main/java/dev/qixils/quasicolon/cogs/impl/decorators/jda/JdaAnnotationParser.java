/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl.decorators.jda;

import dev.qixils.quasicolon.cogs.ApplicationCommand;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;

public final class JdaAnnotationParser {

	public Collection<ApplicationCommand<?>> parse(final Object object) {
		for (final Method method : object.getClass().getDeclaredMethods()) {
			InteractionCommandMethod annotation = method.getAnnotation(InteractionCommandMethod.class);
			if (annotation == null)
				continue;
			if (!method.canAccess(object))
				method.setAccessible(true);
			if (Modifier.isStatic(method.getModifiers()))
				throw new IllegalArgumentException("Decorator methods must not be static");
			// TODO: implement
		}
		return Collections.emptyList(); // TODO: implement
	}
}
