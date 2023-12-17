/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.decorators.slash;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to classes to denote the parent command under which all slash command methods in the class are registered.
 * This will also be prepended to command IDs when fetching translations.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated(forRemoval = true)
public @interface SlashCommandGroup {

	/**
	 * The ID of the command group in your translation file.
	 * <p>
	 * Should be lowercase and alphanumeric.
	 * </p>
	 * See the Javadocs of {@link dev.qixils.quasicolon.locale.TranslationProvider TranslationProvider}
	 * and {@link net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction LocalizationFunction}
	 * for more information on how to use this.
	 *
	 * @return command group ID
	 */
	String value();
}
