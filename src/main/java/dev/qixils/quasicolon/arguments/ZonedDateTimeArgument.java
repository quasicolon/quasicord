package dev.qixils.quasicolon.arguments;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.context.CommandContext;
import dev.qixils.quasicolon.arguments.DurationArgument.DurationParser;
import dev.qixils.quasicolon.error.syntax.NegativeTimeException;
import dev.qixils.quasicolon.error.syntax.UnknownFormatException;
import dev.qixils.quasicolon.error.syntax.UnknownTokenException;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import net.dv8tion.jda.api.utils.TimeUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class ZonedDateTimeArgument<C> extends CommandArgument<C, ZonedDateTime> {

	ZonedDateTimeArgument(
			final boolean required,
			final @NonNull String name,
			final @NonNull Function<@NonNull C, @NonNull Optional<ZoneId>> senderTimezoneMapper,
			final @NonNull ParserMode mode,
			final boolean futureOnly,
			final @NonNull String defaultValue,
			final @NonNull TypeToken<ZonedDateTime> valueType,
			final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
			final @NonNull ArgumentDescription defaultDescription
	) {
		super(required, name, new ZonedDateTimeParser<>(senderTimezoneMapper, mode, futureOnly), defaultValue, valueType, suggestionsProvider, defaultDescription);
	}

	private static boolean canParse(@NonNull DateTimeFormatter formatter, @NonNull String input) {
		try {
			formatter.parseBest(input, ZonedDateTime::from, OffsetDateTime::from, LocalDateTime::from, LocalDate::from, OffsetTime::from, LocalTime::from);
			return true;
		} catch (DateTimeParseException | IllegalArgumentException ignored) {
			return false;
		}
	}

	private static @NonNull ZonedDateTime parseTemporalAccessor(@NonNull TemporalAccessor temporal, @NonNull ZoneId zone) throws IllegalArgumentException {
		if (temporal instanceof ZonedDateTime zdt)
			return zdt;
		else if (temporal instanceof OffsetDateTime odt)
			return odt.atZoneSameInstant(zone);
		else if (temporal instanceof LocalDateTime ldt)
			return ldt.atZone(zone);
		else if (temporal instanceof LocalDate ld)
			return ld.atStartOfDay(zone).plusHours(9); // 9 AM
		else if (temporal instanceof OffsetTime ot) {
			OffsetDateTime odt = ot.atDate(LocalDate.now());
			if (odt.isBefore(OffsetDateTime.now()))
				odt = odt.plusDays(1);
			return odt.atZoneSameInstant(zone);
		} else if (temporal instanceof LocalTime lt) {
			LocalDateTime odt = lt.atDate(LocalDate.now());
			if (odt.isBefore(LocalDateTime.now()))
				odt = odt.plusDays(1);
			return odt.atZone(zone);
		} else {
			throw new IllegalArgumentException("Unknown temporal class: " + temporal.getClass().getName());
		}
	}

	public static final class Builder<C> extends CommandArgument.Builder<C, ZonedDateTime> {

		private @NonNull ParserMode parserMode = ParserMode.QUOTED;
		private @Nullable Function<C, Optional<ZoneId>> senderTimezoneMapper;
		private boolean futureOnly = false;
		private Builder(final @NonNull String name) {
			super(ZonedDateTime.class, name);
		}

		public @NonNull Builder<C> mode(@NonNull ParserMode mode) {
			this.parserMode = mode;
			return this;
		}

		public @NonNull Builder<C> senderTimezoneMapper(@NonNull Function<@NonNull C, @NonNull Optional<ZoneId>> senderTimezoneMapper) {
			this.senderTimezoneMapper = senderTimezoneMapper;
			return this;
		}

		public @NonNull Builder<C> futureOnly(boolean futureOnly) {
			this.futureOnly = futureOnly;
			return this;
		}

		@Override
		public @NonNull CommandArgument<@NonNull C, @NonNull ZonedDateTime> build() {
			if (senderTimezoneMapper == null)
				throw new IllegalStateException("Sender timezone mapper is not set");
			return new ZonedDateTimeArgument<>(isRequired(), getName(), senderTimezoneMapper, parserMode, futureOnly, getDefaultValue(), getValueType(), getSuggestionsProvider(), getDefaultDescription());
		}

	}

	public static final class ZonedDateTimeParser<C> extends AutomaticTypedParser<C, ZonedDateTime> {

		private static final @NonNull ZoneId UTC = ZoneOffset.UTC;
		private static final @NonNull Set<String> IGNORED_TOKENS = Set.of("on", "at");
		private static final @NonNull List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
				// order is of most specific to least specific (ish)
				DateTimeFormatter.ISO_DATE_TIME,
				DateTimeFormatter.ISO_INSTANT,
				DateTimeFormatter.ISO_DATE,
				DateTimeFormatter.ISO_TIME,
				DateTimeFormatter.RFC_1123_DATE_TIME,
				DateTimeFormatter.ISO_WEEK_DATE
		);
		private static final @NonNull Pattern YEAR_PATTERN = Pattern.compile("^\\d{4}$");
		private static final @NonNull Pattern TWO_DIGIT_PATTERN = Pattern.compile("^\\d{1,2}$");
		private static final @NonNull Pattern TIME_OF_DAY_PATTERN = Pattern.compile("^[ap]\\.?m\\.?$", Pattern.CASE_INSENSITIVE);
		private final @NonNull Function<C, Optional<ZoneId>> senderTimezoneMapper;
		private final @NonNull DurationParser<C> durationParser;
		private final boolean futureOnly;
		public ZonedDateTimeParser(final @NonNull Function<@NonNull C, @NonNull Optional<ZoneId>> senderTimezoneMapper, final @NonNull ParserMode mode, final boolean futureOnly) {
			super(mode);
			this.senderTimezoneMapper = senderTimezoneMapper;
			this.futureOnly = futureOnly;
			// to help keep the code a bit cleaner, we allow negative durations in the sub parser
			// and then manually validate the result
			this.durationParser = new DurationParser<>(mode, true);
		}

		protected @NonNull ArgumentParseResult<@NonNull ZonedDateTime> parse(final @NonNull CommandContext<@NonNull C> ctx, final @NonNull List<@NonNull String> input) {
			ZoneId zoneId = senderTimezoneMapper.apply(ctx.getSender()).orElse(UTC);
			ZonedDateTime now = ZonedDateTime.now(zoneId);

			if (input.size() == 1) {
				String element = input.get(0);
				try {
					long number = Long.parseLong(element);
					if (element.length() >= 17 && element.length() <= 19) {
						// input is a discord snowflake
						return validate(ctx, TimeUtil.getTimeCreated(number).atZoneSameInstant(zoneId), now);
					} else {
						// input is a unix timestamp
						ArgumentParseResult<ZonedDateTime> secondsResult = validate(ctx, Instant.ofEpochSecond(number).atZone(zoneId), now);
						if (secondsResult.getFailure().isPresent())
							return validate(ctx, Instant.ofEpochMilli(number).atZone(zoneId), now);
						else
							return secondsResult;
					}
				} catch (NumberFormatException ignored) {
				}
			}

			String fullInput = String.join(" ", input);

			// attempt to parse ISO formatted dates/times
			for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
				try {
					return validate(
							ctx,
							parseTemporalAccessor(formatter.parseBest(fullInput, ZonedDateTime::from, OffsetDateTime::from, LocalDateTime::from, LocalDate::from, OffsetTime::from, LocalTime::from), zoneId),
							now
					);
				} catch (DateTimeParseException | IllegalArgumentException ignored) {
				}
			}

			// attempt to parse duration-like wording (i.e. 'in 30 minutes')
			ArgumentParseResult<Duration> duration = durationParser.parse(ctx, input);
			if (duration.getParsedValue().isPresent()) {
				return validate(ctx, now.plus(duration.getParsedValue().get()), now);
			}

			// attempt to parse complex wording (i.e. 'thursday at 10:45 pm')
			PartialDateTime pdt = new PartialDateTime(now);
			input.removeIf(s -> IGNORED_TOKENS.contains(s.toLowerCase(Locale.ENGLISH)));
			String inputText = String.join(" ", input);
			for (String token : inputText.split(" ")) {
				if (token.isEmpty())
					continue;

				// TODO support some i18n
				token = token.toLowerCase(Locale.ENGLISH);
				if (token.equals("tomorrow")) {
					pdt.fromZDT(pdt.asZDT(zoneId).plusDays(1));
					pdt.setAsMorning();
					pdt.markDateAsSet();
				} else if (token.equals("yesterday")) {
					pdt.fromZDT(pdt.asZDT(zoneId).minusDays(1));
					pdt.setAsMorning();
					pdt.markDateAsSet();
				} else if (token.equals("today"))
					pdt.markDateAsSet();
				else if (!pdt.isYearSet() && YEAR_PATTERN.matcher(token).matches())
					pdt.setYear(Integer.parseInt(token));
				else if (!pdt.isMonthSet()) {
					for (Month month : Month.values()) {
						if (token.startsWith(month.name().toLowerCase(Locale.ENGLISH).substring(0, 3))) {
							pdt.setMonth(month.getValue());
							break;
						}
					}
				} else if (!pdt.isDaySet() && TWO_DIGIT_PATTERN.matcher(token).matches())
					pdt.setDay(Integer.parseInt(token));
				else if (!pdt.isTimeSet() && canParse(DateTimeFormatter.ISO_LOCAL_TIME, token)) {
					LocalTime time = LocalTime.parse(token, DateTimeFormatter.ISO_LOCAL_TIME);
					pdt.setHour(time.getHour());
					pdt.setMinute(time.getMinute());
				} else if (pdt.isTimeSet() && TIME_OF_DAY_PATTERN.matcher(token).matches()) {
					char timeOfDay = token.charAt(0);

					if (timeOfDay == 'a' && pdt.getHour() == 12)
						pdt.setHour(0);
					else if (timeOfDay == 'p' && pdt.getHour() != 12)
						pdt.setHour(pdt.getHour() + 12);
				} else {
					return ArgumentParseResult.failure(new UnknownTokenException(ctx, token));
				}
			}

			if (pdt.isDateSet() || pdt.isTimeSet())
				return validate(ctx, pdt.asZDT(zoneId), now);

			return ArgumentParseResult.failure(new UnknownFormatException(ctx, inputText));
		}

		@NonNull
		private ArgumentParseResult<ZonedDateTime> validate(@NonNull CommandContext<C> ctx,
															@NonNull ZonedDateTime toValidate,
															@NonNull ZonedDateTime now) {
			if (futureOnly && !toValidate.isAfter(now))
				return ArgumentParseResult.failure(new NegativeTimeException(ctx));
			return ArgumentParseResult.success(toValidate);
		}
	}

	@Getter
	private static final class PartialDateTime {
		private int year = 0; // 1-indexed
		private boolean yearSet = false;
		private short month = 0; // 1-indexed
		private boolean monthSet = false;
		private short day = 0; // 1-indexed
		private boolean daySet = false;
		private short hour = -1; // 0-indexed
		private boolean hourSet = false;
		private short minute = -1; // 0-indexed
		private boolean minuteSet = false;

		PartialDateTime() {
		}

		PartialDateTime(@NonNull ZonedDateTime now) {
			fromZDT(now);
		}

		boolean unknownDate() {
			return year == 0 && month == 0 && day == 0;
		}

		public void setYear(int year) {
			this.year = year;
			this.yearSet = true;
		}

		public void setMonth(int month) {
			this.month = (short) month;
			this.monthSet = true;
		}

		public void setDay(int day) {
			this.day = (short) day;
			this.daySet = true;
		}

		public void setHour(int hour) {
			this.hour = (short) hour;
			this.hourSet = true;
		}

		public void setMinute(int minute) {
			this.minute = (short) minute;
			this.minuteSet = true;
		}

		public @NonNull ZonedDateTime asZDT(@NonNull ZoneId zone) {
			return ZonedDateTime.of(year, month, day, hour, minute, 0, 0, zone);
		}

		public void fromZDT(@NonNull ZonedDateTime zdt) {
			year = zdt.getYear();
			month = (short) zdt.getMonthValue();
			day = (short) zdt.getDayOfMonth();
			hour = (short) zdt.getHour();
			minute = (short) zdt.getMinute();
		}

		private void markDateAsSet() {
			daySet = true;
			monthSet = true;
			yearSet = true;
		}

		public boolean isDateSet() {
			return daySet && monthSet && yearSet;
		}

		private void markTimeAsSet() {
			hourSet = true;
			minuteSet = true;
		}

		public boolean isTimeSet() {
			return hourSet && minuteSet;
		}

		private void setAsMorning() {
			hour = 8;
			minute = 0;
		}
	}
}
