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
package org.teamapps.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an event that can get fired.
 * <p>
 * Listeners can be added to this event using the various {@link #addListener(Consumer) addListener(...)} methods.
 *
 * @param <EVENT_DATA> The type of data this event fires.
 */
public class Event<EVENT_DATA> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Event.class);
	private final String source; // for debugging

	protected final List<Consumer<EVENT_DATA>> listeners = new CopyOnWriteArrayList<>();
	private final EventListenerRegistrationStatusListener registrationListener;
	private EVENT_DATA lastEventData;

	public Event() {
		this(null);
	}

	public Event(EventListenerRegistrationStatusListener registrationListener) {
		this.registrationListener = registrationListener;
		StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
		this.source = stackTraceElement.getFileName() + stackTraceElement.getLineNumber();
	}

	public Disposable addListener(Consumer<EVENT_DATA> listener) {
		boolean wasEmpty = listeners.isEmpty();
		listeners.add(listener);
		if (registrationListener != null && wasEmpty) {
			registrationListener.listeningStatusChanged(true);
		}
		return () -> {
			boolean wasRemoved = listeners.remove(listener);
			if (registrationListener != null && wasRemoved && listeners.isEmpty()) {
				registrationListener.listeningStatusChanged(false);
			}
		};
	}

	public Disposable addListener(SelfDisposingEventListener<EVENT_DATA> listener) {
		AtomicReference<Disposable> disposable = new AtomicReference<>();
		disposable.set(addListener(e -> listener.handle(e, disposable.get())));
		return disposable.get();
	}

	public Disposable addListener(Runnable listener) {
		return addListener(new RunnableWrapper<>(listener));
	}

	public List<Consumer<EVENT_DATA>> getListeners() {
		return listeners;
	}

	public void fire(EVENT_DATA eventData) {
		this.lastEventData = eventData;
		for (Consumer<EVENT_DATA> listener : listeners) {
			invokeListener(eventData, listener);
		}
	}

	public void fireIgnoringExceptions(EVENT_DATA eventData) {
		this.lastEventData = eventData;
		for (Consumer<EVENT_DATA> listener : listeners) {
			try {
				invokeListener(eventData, listener);
			} catch (Exception e) {
				LOGGER.error("Error while calling event handler. Ignoring exception.", e);
			}
		}
	}

	/**
	 * May get overridden.
	 */
	protected void invokeListener(EVENT_DATA eventData, Consumer<EVENT_DATA> listener) {
		listener.accept(eventData);
	}

	public void fireIfChanged(EVENT_DATA eventData) {
		if (!Objects.equals(lastEventData, eventData)) {
			fire(eventData);
		}
	}

	public <T> Event<T> converted(Function<EVENT_DATA, T> converter) {
		Event<T> newEvent = new Event<>();
		addListener(data -> newEvent.fire(converter.apply(data)));
		return newEvent;
	}

	public void fire() {
		fire(null);
	}

	protected static class RunnableWrapper<T> implements Consumer<T> {

		private final Runnable runnable;

		public RunnableWrapper(Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public void accept(T eventData) {
			runnable.run();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			RunnableWrapper<?> that = (RunnableWrapper<?>) o;
			return runnable.equals(that.runnable);
		}

		@Override
		public int hashCode() {
			return Objects.hash(runnable);
		}
	}

}
