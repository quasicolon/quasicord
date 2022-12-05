/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators.slash;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify the default permissions required to use a {@link SlashCommand}.
 * Any slash command that is not annotated with this will be available to everyone by default.
 * Note that moderators can override these permissions.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultPermissions {

	/**
	 * The permissions required to use this command by default.
	 *
	 * @return default permissions
	 */
	Permission[] value();
}
