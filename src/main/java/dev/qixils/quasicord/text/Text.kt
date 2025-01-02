/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.text

import dev.qixils.quasicord.Key
import dev.qixils.quasicord.locale.Context
import dev.qixils.quasicord.locale.LocaleProvider
import net.xyzsd.plurals.PluralRuleType
import org.jetbrains.annotations.Contract
import reactor.core.publisher.Mono
import java.util.*
import java.util.function.Supplier

/**
 * A container for text which may optionally be localized into other languages.
 */
@FunctionalInterface
fun interface Text {
    /**
     * Fetches the default localized string for this text. Usage of this method is generally
     * discouraged, except when logging information for the bot's host.
     *
     * @return default localized string for this text
     */
    fun asString(): String {
        return asString(LocaleProvider.instance.defaultLocale)
    }

    /**
     * Fetches the localized string for this text according to the provided [Context].
     *
     * @param context        the [Context] to fetch the [Locale] from
     * @param localeProvider the [LocaleProvider] to fetch the context's [Locale] from
     * @return a [Mono] that will emit the localized string for this text
     */
    /**
     * Fetches the localized string for this text according to the provided [Context].
     *
     * This uses the [default][LocaleProvider.getInstance] [LocaleProvider] to fetch
     * the context's [Locale].
     *
     * @param context the [Context] to fetch the [Locale] from
     * @return a [Mono] that will emit the localized string for this text
     * @see .asString
     */
    suspend fun asString(context: Context, localeProvider: LocaleProvider = LocaleProvider.instance): String {
        return asString(context.locale(localeProvider))
    }

    /**
     * Fetches the localized string for this text according to the provided [Locale].
     *
     * @param locale the [Locale] to localize with
     * @return localized string for this text
     */
    fun asString(locale: Locale): String

    companion object {
        // static constructors and builders
        /**
         * Creates a literal [Text] instance from the provided string.
         *
         * @param text the literal string text
         * @return new [Text] instance
         */
        fun literal(text: String): Text {
            return LiteralText(text)
        }

        /**
         * Creates a new builder for a [SingleLocalizableText].
         *
         * @return new [SingleLocalizableText.Builder] instance
         */
        fun single(): SingleLocalizableText.Builder {
            return SingleLocalizableText.Builder()
        }

        /**
         * Creates a [SingleLocalizableText] instance from the provided [Key] and arguments.
         *
         * @param key the [Key] to localize
         * @param args the arguments to format the localized string with
         * @return new [SingleLocalizableText] instance
         */
		@JvmStatic
		fun single(key: Key, vararg args: Any?): SingleLocalizableText {
            return SingleLocalizableText(key, args)
        }

        /**
         * Creates a new builder for a [PluralLocalizableText].
         *
         * @return new [PluralLocalizableText.Builder] instance
         */
		@JvmStatic
		fun plural(): PluralLocalizableText.Builder {
            return PluralLocalizableText.Builder()
        }

		/**
		 * Creates a [PluralLocalizableText] instance from the provided [Key], arguments,
		 * quantity, and plural rule type.
		 *
		 * @param quantity the quantity used to determine the plural form
		 * @param ruleType the [PluralRuleType] to use to determine the plural form
		 * @param key the [Key] to localize
		 * @param args the arguments to format the localized string with
		 */
		fun plural(quantity: Int, ruleType: PluralRuleType, key: Key, vararg args: Any?): PluralLocalizableText {
			return plural(quantity.toLong(), ruleType, key, args)
		}

        /**
         * Creates a [PluralLocalizableText] instance from the provided [Key], arguments,
         * quantity, and plural rule type.
         *
         * @param quantity the quantity used to determine the plural form
         * @param ruleType the [PluralRuleType] to use to determine the plural form
         * @param key the [Key] to localize
         * @param args the arguments to format the localized string with
         */
        fun plural(quantity: Long, ruleType: PluralRuleType, key: Key, vararg args: Any?): PluralLocalizableText {
            return PluralLocalizableText(quantity, ruleType, key, *args)
        }

        /**
         * Localizes an array of objects.
         *
         *
         * This converts any [Text] instances to their localized string equivalents.
         * Other objects are left untouched.
         *
         * @param args the array of objects to localize
         * @param locale the [Locale] to localize with
         * @return a new array of localized objects
         */
        fun localizeArgs(args: Array<out Any?>, locale: Locale): Array<out Any?> {
            val localizedArgs = arrayOfNulls<Any>(args.size)
            for (i in args.indices) localizedArgs[i] = localizeArg(args[i], locale)
            return localizedArgs
        }

        /**
         * Localizes an object.
         *
         *
         * This converts any [Text] instances to their localized string equivalents.
         * Other objects are left untouched.
         *
         * @param arg    the object to localize
         * @param locale the [Locale] to localize with
         * @return the localized string
         */
        @Contract("null, _ -> null")
        fun localizeArg(arg: Any?, locale: Locale): Any? {
            if (arg is Text) return arg.asString(locale)
            if (arg is Supplier<*>) return localizeArg(arg.get(), locale)
            return arg
        }
    }
}
