package dev.qixils.quasicolon.text;

import dev.qixils.quasicolon.locale.Context;
import dev.qixils.quasicolon.locale.LocaleProvider;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

/**
 * A simple object to store text.
 */
public interface Text {

	/**
	 * Fetches the default localized string for this text.
	 *
	 * @return default localized string for this text
	 */
	default @NonNull String asString() {
		return asString(Context.EMPTY);
	}

	/**
	 * Fetches the localized string for this text according to the provided {@link Context}.
	 *
	 * @param context the {@link Context} to fetch the {@link Locale} from
	 * @return localized string for this text
	 */
	default @NonNull String asString(@NonNull Context context) {
		return asString(context, LocaleProvider.getInstance());
	}

	/**
	 * Fetches the localized string for this text according to the provided {@link Context}.
	 *
	 * @param context        the {@link Context} to fetch the {@link Locale} from
	 * @param localeProvider the {@link LocaleProvider} to fetch the context's {@link Locale} from
	 * @return localized string for this text
	 */
	default @NonNull String asString(@NonNull Context context, @NonNull LocaleProvider localeProvider) {
		return asString(context.locale(localeProvider));
	}

	/**
	 * Fetches the localized string for this text according to the provided {@link Locale}.
	 *
	 * @param locale the {@link Locale} to localize with
	 * @return localized string for this text
	 */
	@NonNull String asString(@NonNull Locale locale);

	// TODO: create builders
	// TODO: expose helper method in QuasicolonBot for creating builders with the TranslationManager pre-set
}
