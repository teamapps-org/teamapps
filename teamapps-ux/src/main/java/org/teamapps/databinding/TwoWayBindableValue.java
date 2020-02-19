/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.databinding;

import org.teamapps.event.Event;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface TwoWayBindableValue<T> extends ObservableValue<T>, MutableValue<T> {

	// ------- utility -------

	default void bindTwoWays(TwoWayBindableValue<T> other) {
		DataBindings.bindTwoWays(this, other);
	}

	// === static ===

	public static <T> TwoWayBindableValue<T> create(Event<T> changeEvent, Supplier<T> supplier, Consumer<T> consumer) {
		return DataBindings.createTwoWayBindable(changeEvent, supplier, consumer);
	}

	public static <T> TwoWayBindableValue<T> create(Event<T> changeEvent, Consumer<T> consumer) {
		return DataBindings.createTwoWayBindable(changeEvent, consumer);
	}
}
