/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

@Getter
@RequiredArgsConstructor
public abstract class AbstractConverter<I, O> implements Converter<I, O> {

	private final @NonNull Class<I> inputClass;
	private final @NonNull Class<O> outputClass;
}
