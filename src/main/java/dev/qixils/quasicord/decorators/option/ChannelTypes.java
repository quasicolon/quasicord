/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.decorators.option;

import net.dv8tion.jda.api.entities.channel.ChannelType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes which {@link ChannelType}s are supported by an {@link Option} of
 * {@link Option#type() type} {@link net.dv8tion.jda.api.interactions.commands.OptionType#CHANNEL CHANNEL}.
 *
 * @see Option
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChannelTypes {

	/**
	 * The supported channel types.
	 *
	 * @return supported channel types
	 */
	ChannelType[] value();
}
