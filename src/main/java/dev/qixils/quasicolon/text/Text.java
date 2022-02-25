package dev.qixils.quasicolon.text;

import dev.qixils.quasicolon.Key;
import dev.qixils.quasicolon.locale.Context;
import dev.qixils.quasicolon.locale.LocaleProvider;
import net.xyzsd.plurals.PluralRuleType;
import org.checkerframework.checker.nullness.qual.NonNull;
import reactor.core.publisher.Mono;

import java.util.Locale;

/**
 * A simple object to store text.
 */
public interface Text {

	/**
	 * Fetches the default localized string for this text. Usage of this method is generally
	 * discouraged, except when logging information for the bot's host.
	 *
	 * @return default localized string for this text
	 */
	default @NonNull String asString() {
		return asString(LocaleProvider.getInstance().defaultLocale());
	}

	/**
	 * Fetches the localized string for this text according to the provided {@link Context}.
	 * <p>
	 * This uses the {@link LocaleProvider#getInstance() default} {@link LocaleProvider} to fetch
	 * the context's {@link Locale}.
	 *
	 * @param context the {@link Context} to fetch the {@link Locale} from
	 * @return a {@link Mono} that will emit the localized string for this text
	 * @see #asString(Context, LocaleProvider)
	 */
	default @NonNull Mono<String> asString(@NonNull Context context) {
		return asString(context, LocaleProvider.getInstance());
	}

	/**
	 * Fetches the localized string for this text according to the provided {@link Context}.
	 *
	 * @param context        the {@link Context} to fetch the {@link Locale} from
	 * @param localeProvider the {@link LocaleProvider} to fetch the context's {@link Locale} from
	 * @return a {@link Mono} that will emit the localized string for this text
	 */
	default @NonNull Mono<String> asString(@NonNull Context context, @NonNull LocaleProvider localeProvider) {
		return context.locale(localeProvider).map(this::asString);
	}

	/**
	 * Fetches the localized string for this text according to the provided {@link Locale}.
	 *
	 * @param locale the {@link Locale} to localize with
	 * @return localized string for this text
	 */
	@NonNull String asString(@NonNull Locale locale);

	// static constructors and builders

	/**
	 * Creates a literal {@link Text} instance from the provided string.
	 *
	 * @param text the literal string text
	 * @return new {@link Text} instance
	 */
	static @NonNull Text literal(@NonNull String text) {
		return new LiteralText(text);
	}

	/**
	 * Creates a new builder for a {@link SingleLocalizableText}.
	 *
	 * @return new {@link SingleLocalizableText.Builder} instance
	 */
	static SingleLocalizableText.@NonNull Builder single() {
		return new SingleLocalizableText.Builder();
	}

	/**
	 * Creates a {@link SingleLocalizableText} instance from the provided {@link Key} and arguments.
	 *
	 * @param key the {@link Key} to localize
	 * @param args the arguments to format the localized string with
	 * @return new {@link SingleLocalizableText} instance
	 */
	static SingleLocalizableText single(@NonNull Key key, Object... args) {
		return new SingleLocalizableText(key, args);
	}

	/**
	 * Creates a new builder for a {@link PluralLocalizableText}.
	 *
	 * @return new {@link PluralLocalizableText.Builder} instance
	 */
    static PluralLocalizableText.@NonNull Builder plural() {
		return new PluralLocalizableText.Builder();
	}

	/**
	 * Creates a {@link PluralLocalizableText} instance from the provided {@link Key}, arguments,
	 * quantity, and plural rule type.
	 *
	 * @param quantity the quantity used to determine the plural form
	 * @param ruleType the {@link PluralRuleType} to use to determine the plural form
	 * @param key the {@link Key} to localize
	 * @param args the arguments to format the localized string with
	 */
	static PluralLocalizableText plural(int quantity, @NonNull PluralRuleType ruleType, @NonNull Key key, Object... args) {
		return new PluralLocalizableText(quantity, ruleType, key, args);
	}

	/**
	 * Localizes an array of objects.
	 * <p>
	 * This converts any {@link Text} instances to their localized string equivalents.
	 * Other objects are left untouched.
	 *
	 * @param args the array of objects to localize
	 * @param locale the {@link Locale} to localize with
	 * @return a new array of localized objects
	 */
	static Object @NonNull [] localizeArgs(Object @NonNull [] args, @NonNull Locale locale) {
		Object[] localizedArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (arg instanceof Text text) {
				localizedArgs[i] = text.asString(locale);
			} else {
				localizedArgs[i] = arg;
			}
		}
		return localizedArgs;
	}
}
