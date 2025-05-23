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
package org.teamapps.projector.databinding;

import org.teamapps.projector.event.ProjectorEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class supplements {@link org.teamapps.commons.databinding.DataBindings} by
 * creating Projector-specific {@link org.teamapps.commons.databinding.TwoWayBindableValue} instances.
 * <p>
 * This allows for flexible data binding solutions that properly integrate with the Projector's
 * {@link org.teamapps.projector.session.SessionContext} propagation.
 */
public final class ProjectorDataBindings {

	/**
	 * Creates a new {@link ProjectorTwoWayBindableValue} that delegates getting and setting
	 * operations to the provided supplier and consumer functions.
	 * <p>
	 * This method creates an instance of {@link ProjectorTwoWayBindableValue}
	 * that uses the provided supplier for retrieving the current value and the provided
	 * consumer for setting a new value. The change event is used for notifying listeners
	 * about value changes.
	 *
	 * @param <T>         the type of the value
	 * @param changeEvent the event that will be fired when the value changes
	 * @param supplier    the supplier function used to get the current value
	 * @param consumer    the consumer function used to set a new value
	 * @return a new {@link ProjectorTwoWayBindableValue} instance that delegates to the provided functions
	 */
	public static <T> ProjectorTwoWayBindableValue<T> createTwoWayBindable(ProjectorEvent<T> changeEvent, Supplier<T> supplier, Consumer<T> consumer) {
		return new ProjectorTwoWayBindableValue<T>() {
			@Override
			public ProjectorEvent<T> onChange() {
				return changeEvent;
			}

			@Override
			public T get() {
				return supplier.get();
			}

			@Override
			public void set(T value) {
				consumer.accept(value);
			}
		};
	}

	/**
	 * Creates a new {@link ProjectorTwoWayBindableValue} that delegates setting operations
	 * to the provided consumer function and tracks the last seen value from the change event.
	 * <p>
	 * This method creates an instance of {@link ProjectorTwoWayBindableValue}
	 * that uses the provided consumer for setting a new value. The current value is tracked
	 * by listening to the change event and storing the last seen value.
	 *
	 * @param <T>         the type of the value
	 * @param changeEvent the event that will be fired when the value changes and used to track the current value
	 * @param consumer    the consumer function used to set a new value
	 * @return a new {@link ProjectorTwoWayBindableValue} instance that delegates setting to the provided function
	 */
	public static <T> ProjectorTwoWayBindableValue<T> createTwoWayBindable(ProjectorEvent<T> changeEvent, Consumer<T> consumer) {
		return new ProjectorTwoWayBindableValue<T>() {

			private T lastSeenValue;

			{
				changeEvent.addListener(t -> this.lastSeenValue = t);
			}

			@Override
			public ProjectorEvent<T> onChange() {
				return changeEvent;
			}

			@Override
			public T get() {
				return lastSeenValue;
			}

			@Override
			public void set(T value) {
				consumer.accept(value);
			}
		};
	}

}
