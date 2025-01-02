/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.locale.translation

/**
 * A translation that is not known to the library. This implements the methods of the two main
 * translation interfaces but will return the translation key instead of a translation.
 */
interface UnknownTranslation : SingleTranslation, PluralTranslation
