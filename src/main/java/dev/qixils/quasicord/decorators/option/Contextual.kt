/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators.option

/**
 * Annotations for parameters that represent data obtained from an
 * [Interaction][net.dv8tion.jda.api.interactions.Interaction].
 * For example, a [User][net.dv8tion.jda.api.entities.User] parameter annotated with this annotation will be
 * provided with the user who invoked the interaction.
 *
 * @see ConvertWith
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Contextual
