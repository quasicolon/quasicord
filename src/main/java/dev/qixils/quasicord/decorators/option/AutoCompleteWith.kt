/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.decorators.option

import dev.qixils.quasicord.autocomplete.AutoCompleter
import kotlin.reflect.KClass

/**
 * Denotes the class that should be used to generate tab completions for a [Option].
 * This is only to be used by options whose [type][Option.type]
 * [supports choices][net.dv8tion.jda.api.interactions.commands.OptionType.canSupportChoices].
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class AutoCompleteWith(

    /**
     * The class to use for generating auto-complete suggestions.
     *
     * @return auto-completer class
     */
    val value: KClass<out AutoCompleter>
)
