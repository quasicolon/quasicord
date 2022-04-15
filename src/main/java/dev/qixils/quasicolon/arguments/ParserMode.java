/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.arguments;

/**
 * Determines how a {@link AbstractTypedParser} command argument is parsed.
 */
public enum ParserMode {
    /**
     * Parses a quoted string if available, else the first token.
     */
    QUOTED,
    /**
     * Parses tokens until the parser reaches an unknown token.
     */
    GREEDY,
    /**
     * Parses all available tokens.
     */
    ALL
}
