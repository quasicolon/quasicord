package dev.qixils.quasicolon.cogs.impl.decorators.jda;

import net.dv8tion.jda.api.interactions.commands.Command;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods that represent interaction commands.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InteractionCommandMethod {

	/**
	 * The command's syntax.
	 *
	 * @return command syntax
	 */
	@NonNull String value();

	/**
	 * The type of interaction command.
	 *
	 * @return interaction command type
	 */
	Command.@NonNull Type type() default Command.Type.MESSAGE;
}
