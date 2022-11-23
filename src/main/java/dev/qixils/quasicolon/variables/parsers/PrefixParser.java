/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.variables.parsers;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.Quasicord;
import dev.qixils.quasicolon.text.Text;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PrefixParser extends CollectionParser<Set<String>, String> {
	public PrefixParser(@NotNull Quasicord bot) {
		super(bot, new StringParser(bot), ' ', HashSet::new);
	}

	@Override
	public @NotNull CompletableFuture<Set<String>> parseText(@Nullable Message context, @NotNull String humanText) {
		return super.parseText(context, humanText).thenApply(prefixes -> {
			List<String> sortedPrefixes = new ArrayList<>(prefixes);
			sortedPrefixes.removeIf(String::isEmpty);
			sortedPrefixes.sort(Comparator.comparingInt(String::length));
			Set<String> processedPrefixes = new HashSet<>(sortedPrefixes.size());

			boolean invalidPrefixWarning = context == null;
			for (String prefix : sortedPrefixes) {
				boolean validPrefix = true;
				for (String processedPrefix : processedPrefixes) {
					if (prefix.startsWith(processedPrefix)) {
						if (!invalidPrefixWarning) {
							invalidPrefixWarning = true;
							Text.single(Key.library("invalid_prefix_warning")).sendAsReplyTo(context).queue();
						}
						validPrefix = false;
						break;
					}
				}
				if (validPrefix)
					processedPrefixes.add(prefix);
			}
			return processedPrefixes;
		});
	}
}
