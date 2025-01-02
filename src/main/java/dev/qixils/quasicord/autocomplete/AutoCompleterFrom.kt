/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.autocomplete

import net.dv8tion.jda.api.interactions.callbacks.IAutoCompleteCallback
import net.dv8tion.jda.api.interactions.commands.Command

/**
 * An object which supplies pre-defined auto-complete suggestions for [IAutoCompleteCallback]s.
 */
class AutoCompleterFrom : AutoCompleter {
    private val choices: List<Command.Choice>

    constructor(choices: List<Command.Choice>) {
        this.choices = choices.toList()
    }

    constructor(vararg choices: Command.Choice) {
        this.choices = choices.toList()
    }

    override suspend fun getSuggestions(event: IAutoCompleteCallback): List<Command.Choice> {
        return choices
    }
}
