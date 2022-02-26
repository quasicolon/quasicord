package dev.qixils.quasicolon.arguments;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.context.CommandContext;
import dev.qixils.quasicolon.error.syntax.NegativeTimeException;
import dev.qixils.quasicolon.error.syntax.UnknownFormatException;
import dev.qixils.quasicolon.error.syntax.UnknownTokenException;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

// TODO: javadocs
// TODO: builder
public final class DurationArgument<C> extends CommandArgument<C, Duration> {

	DurationArgument(
			final boolean required,
			final @NonNull String name,
			final @NonNull ParserMode mode,
			final boolean allowNegative,
			final @NonNull String defaultValue,
			final @NonNull TypeToken<Duration> valueType,
			final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
			final @NonNull ArgumentDescription defaultDescription
	) {
		super(required, name, new DurationParser<>(mode, allowNegative), defaultValue, valueType, suggestionsProvider, defaultDescription);
	}

	public static final class DurationParser<C> extends AutomaticTypedParser<C, Duration> {

		private static final @NonNull Set<String> RELATIVE_TIME_IGNORED_TOKENS = Set.of("in");
		private static final @NonNull Pattern RELATIVE_TIME_PATTERN = Pattern.compile("^\\d+[A-Za-z]+");
		private final boolean allowNegative;

		DurationParser(@NonNull ParserMode mode, boolean allowNegative) {
			super(mode);
			this.allowNegative = allowNegative;
		}

		@Override
		protected @NonNull ArgumentParseResult<@NonNull Duration> parseGreedy(@NonNull CommandContext<@NonNull C> ctx, @NonNull Queue<@NonNull String> inputQueue) {
			List<String> input = new ArrayList<>(inputQueue);
			ArgumentParseResult<Duration> result = parse(ctx, input);
			while (input.size() != inputQueue.size()) {
				inputQueue.remove();
			}
			return result;
		}

		@Override
		protected @NonNull ArgumentParseResult<@NonNull Duration> parse(@NonNull CommandContext<@NonNull C> ctx, @NonNull List<@NonNull String> arguments) {
			assert !arguments.isEmpty();
			arguments.removeIf(s -> RELATIVE_TIME_IGNORED_TOKENS.contains(s.toLowerCase(Locale.ENGLISH)));

			boolean negative = false;
			if (arguments.get(0).startsWith("-")) {
				arguments.set(0, arguments.get(0).substring(1));
				negative = true;
			}
			if (arguments.get(arguments.size()-1).equalsIgnoreCase("ago")) {
				arguments.remove(arguments.size()-1);
				negative = !negative;
			}

			if (negative && !allowNegative)
				return ArgumentParseResult.failure(new NegativeTimeException(ctx));

			String fullInput = String.join("", arguments);
			if (!RELATIVE_TIME_PATTERN.matcher(fullInput).matches()) {
				return ArgumentParseResult.failure(new UnknownFormatException(ctx, String.join(" ", arguments)));
			}

			// vars for parsing the duration
			Duration duration = Duration.ZERO;
			StringBuilder nextToken = new StringBuilder();
			boolean buildingAmount = true;
			String amountToken = null;
			String unitToken;
			// vars for parsing the terms/tokens used fom the arg list
			StringBuilder term = new StringBuilder();
			int termIdx = 0;
			int parsedTermIdx = 0;

			for (char chr : fullInput.toCharArray()) {
				term.append(chr);
				if (term.toString().equals(arguments.get(termIdx))) {
					term = new StringBuilder();
					++termIdx;
				}
				if (Character.isWhitespace(chr)) {
					continue;
				}

				boolean isDigit = Character.isDigit(chr);
				if (isDigit && !buildingAmount) {
					// finished parsing unit of time (and duration of time)
					buildingAmount = true;
					unitToken = nextToken.toString();
					nextToken = new StringBuilder();
					// now attempt to find the corresponding RelativeTimeUnit
					RelativeTimeUnit unit = RelativeTimeUnit.of(unitToken);
					if (unit == null) {
						if (mode == ParserMode.GREEDY)
							break;
						else
							return ArgumentParseResult.failure(new UnknownTokenException(ctx, unitToken));
					}
					// add relative time to current time object
					duration = unit.addTimeTo(duration, Long.parseLong(amountToken)); // theoretically this shouldn't error
					parsedTermIdx = termIdx;
					// reset variables
					amountToken = null;
				} else if (!isDigit && buildingAmount) {
					// finished parsing duration of time, save it & reset the builder
					buildingAmount = false;
					amountToken = nextToken.toString();
					nextToken = new StringBuilder();
				}
				nextToken.append(chr);
			}

			if (negative)
				duration = Duration.ZERO.minus(duration);

			while (parsedTermIdx > 0) {
				arguments.remove(0);
				parsedTermIdx--;
			}

			return ArgumentParseResult.success(duration);
		}
	}

	private enum RelativeTimeUnit implements IToken {
		SECOND(Duration::plusSeconds, "s", "sec", "second"),
		MINUTE(Duration::plusMinutes, "m", "min", "minute"),
		HOUR(Duration::plusHours, "h", "hr", "hour"),
		DAY(Duration::plusDays, "d", "day"),
		WEEK((duration, time) -> duration.plusDays(time*7), "w", "week");

		private final @NonNull BiFunction<@NonNull Duration, @NonNull Long, @NonNull Duration> addTimeFunction;
		private final Token @NonNull [] tokens;

		RelativeTimeUnit(
				@NonNull BiFunction<@NonNull Duration, @NonNull Long, @NonNull Duration> addTimeFunction,
				String... tokens
		) {
			this.addTimeFunction = addTimeFunction;
			this.tokens = new Token[tokens.length];

			for (byte i = 0; i < tokens.length; i++) {
				String token = tokens[i];
				// treat tokens like "d" as singular and "day" as plural
				this.tokens[i] = token.length() == 1 ? Token.ofSingular(token) : Token.ofPlural(token);
			}
		}

		public boolean matches(@NotNull String input) {
			for (Token token : tokens) {
				if (token.matches(input))
					return true;
			}
			return false;
		}

		public @NonNull Duration addTimeTo(@NonNull Duration originalTime, long duration) {
			return addTimeFunction.apply(originalTime, duration);
		}

		public static @Nullable RelativeTimeUnit of(@NonNull String token) {
			for (RelativeTimeUnit unit : values()) {
				if (unit.toLowerMatches(token))
					return unit;
			}
			return null;
		}
	}
}
