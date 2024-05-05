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

import java.util.Objects;
import java.util.function.Supplier;

public interface ObservableValue<T> {

	ProjectorEvent<T> onChanged();

	T get();

	// ------- utility -------

	default boolean valueEquals(T other) {
		return Objects.equals(get(), other);
	}

	default void bindWritingTo(MutableValue<T> mutableValue) {
		DataBindings.bindOneWay(this, mutableValue);
	}

//	default <T2> ObservableValue<T2> transformed(Function<T, T2> transformation) {
//		return DataBindings.createObservableValue(onChanged(), () -> null);
//	}

	// === static ===

	public static <T> ObservableValue<T> fromEmptyEvent(ProjectorEvent<?> changeEvent, Supplier<T> provider) {
		return DataBindings.createObservableValueWithEmptyEvent(changeEvent, provider);
	}

	public static <T> ObservableValue<T> fromEvent(ProjectorEvent<T> changeEvent, Supplier<T> provider) {
		return DataBindings.createObservableValue(changeEvent, provider);
	}
	public static <T> ObservableValue<T> fromCachedEventValues(ProjectorEvent<T> changeEvent) {
		return DataBindings.createObservableValue(changeEvent);
	}

//	static <T> ObservableValue<T> from(Event<?> changeEvent, Supplier<T> provider) {
//		return DataBindings.createObservableValue(changeEvent, provider);
//	}
}
