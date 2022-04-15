package dev.qixils.quasicolon.cogs.impl.decorators.jda;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;

public final class JdaAnnotationParser {

	public Collection<CommandData> parse(final Object object) {
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
