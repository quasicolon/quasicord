/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.autocomplete

import net.dv8tion.jda.api.interactions.callbacks.IAutoCompleteCallback
import net.dv8tion.jda.api.interactions.commands.Command
import reactor.core.publisher.Flux

/**
 * An object which supplies pre-defined auto-complete suggestions for [IAutoCompleteCallback]s.
 */
class AutoCompleterFrom : AutoCompleter {
    private val choices: Flux<Command.Choice>

    constructor(choices: MutableList<Command.Choice?>) {
        this.choices = Flux.fromIterable<Command.Choice?>(choices)
    }

    constructor(vararg choices: Command.Choice?) {
        this.choices = Flux.fromArray<Command.Choice?>(choices)
    }

    override fun getSuggestions(event: IAutoCompleteCallback): Flux<Command.Choice> {
        return choices
    }
}
