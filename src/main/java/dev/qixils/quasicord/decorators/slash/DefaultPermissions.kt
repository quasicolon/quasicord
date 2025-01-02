/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators.slash

import net.dv8tion.jda.api.Permission
import java.lang.annotation.Inherited

/**
 * Annotation used to specify the default permissions required to use a [SlashCommand].
 * Any slash command that is not annotated with this will be available to everyone by default.
 * Note that moderators can override these permissions.
 */
@Inherited
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultPermissions(

    /**
     * The permissions required to use this command by default.
     *
     * @return default permissions
     */
    vararg val value: Permission
)
