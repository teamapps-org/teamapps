/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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

import org.teamapps.projector.event.ProjectorEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface TwoWayBindableValue<T> extends ObservableValue<T>, MutableValue<T> {

	// ------- utility -------

	default void bindTwoWays(TwoWayBindableValue<T> other) {
		DataBindings.bindTwoWays(this, other);
	}

	// === static ===

	static <T> TwoWayBindableValue<T> create() {
		return new TwoWayBindableValueImpl<>();
	}

	static <T> TwoWayBindableValue<T> create(T initialValue) {
		return new TwoWayBindableValueImpl<>(initialValue);
	}

	static <T> TwoWayBindableValue<T> create(ProjectorEvent<T> changeEvent, Supplier<T> getter, Consumer<T> setter) {
		return DataBindings.createTwoWayBindable(changeEvent, getter, setter);
	}

	static <T> TwoWayBindableValue<T> create(ProjectorEvent<T> changeEvent, Consumer<T> setter) {
		return DataBindings.createTwoWayBindable(changeEvent, setter);
	}
}
