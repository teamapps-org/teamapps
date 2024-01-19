/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

public final class DataBindings {

	public static <T> ObservableValue<T> createObservableValueWithEmptyEvent(Event<?> changeEvent, Supplier<T> provider) {
		return new ObservableValue<T>() {
			@Override
			public Event<T> onChanged() {
				Event<T> eventWithData = new Event<>();
				changeEvent.addListener(o -> eventWithData.fire(get()));
				return eventWithData;
			}

			@Override
			public T get() {
				return provider.get();
			}
		};
	}

	public static <T> ObservableValue<T> createObservableValue(Event<T> changeEvent, Supplier<T> provider) {
		return new ObservableValue<T>() {
			@Override
			public Event<T> onChanged() {
				return changeEvent;
			}

			@Override
			public T get() {
				return provider.get();
			}
		};
	}

	public static <T> ObservableValue<T> createObservableValue(Event<T> changeEvent) {
		return new ObservableValue<T>() {

			private T lastSeenValue;

			{
				changeEvent.addListener(t -> this.lastSeenValue = t);
			}

			@Override
			public Event<T> onChanged() {
				return changeEvent;
			}

			@Override
			public T get() {
				return lastSeenValue;
			}
		};
	}

	public static <T> MutableValue<T> createMutableValue(Consumer<T> consumer) {
		return consumer::accept;
	}

	public static <T> TwoWayBindableValue<T> createTwoWayBindable(Event<T> changeEvent, Supplier<T> supplier, Consumer<T> consumer) {
		return new TwoWayBindableValue<T>() {
			@Override
			public Event<T> onChanged() {
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

	public static <T> TwoWayBindableValue<T> createTwoWayBindable(Event<T> changeEvent, Consumer<T> consumer) {
		return new TwoWayBindableValue<T>() {

			private T lastSeenValue;

			{
				changeEvent.addListener(t -> this.lastSeenValue = t);
			}

			@Override
			public Event<T> onChanged() {
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

	public static <T> void bindOneWay(ObservableValue<T> observableValue, MutableValue<T> mutableValue) {
		mutableValue.set(observableValue.get()); // initialize value
		observableValue.onChanged().addListener(value -> mutableValue.set(observableValue.get()));
	}

	public static <T> void bindTwoWays(TwoWayBindableValue<T> bindable1, TwoWayBindableValue<T> bindable2) {
		NonRecursiveEventListenerBuilder nonRecursiveEventListenerBuilder = new NonRecursiveEventListenerBuilder();
		bindable1.onChanged().addListener(nonRecursiveEventListenerBuilder.create(aVoid -> bindable2.set(bindable1.get())));
		bindable2.onChanged().addListener(nonRecursiveEventListenerBuilder.create(aVoid -> bindable1.set(bindable2.get())));
		bindable2.set(bindable1.get());
	}
}
