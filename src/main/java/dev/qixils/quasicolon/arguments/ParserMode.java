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