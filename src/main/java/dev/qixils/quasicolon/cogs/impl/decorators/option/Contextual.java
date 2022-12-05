/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.cogs.impl.decorators.option;

// TODO: optional value denoting a custom class for option parsing

/**
 * Annotations for parameters that represent data obtained from an
 * {@link net.dv8tion.jda.api.interactions.Interaction Interaction}.
 * For example, a {@link net.dv8tion.jda.api.entities.User User} parameter annotated with this annotation will be
 * provided with the user who invoked the interaction.
 */
public @interface Contextual {
}
