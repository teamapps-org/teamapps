/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class Event<EVENT_DATA> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Event.class);
	private final String source; // for debugging

	private List<EventListener<EVENT_DATA>> listeners = new CopyOnWriteArrayList<>();
	private EVENT_DATA lastEventData;

	public Event() {
		StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
		this.source = stackTraceElement.getFileName() + stackTraceElement.getLineNumber();
	}


	public void addListener(EventListener<EVENT_DATA> listener) {
		SessionContext currentSessionContext = CurrentSessionContext.getOrNull();
		listeners.add(new SessionContextAwareEventListener<>(currentSessionContext, listener));
		if (currentSessionContext != null) {
			removeWhenSessionDestroyed(listener, currentSessionContext);
		}
	}

	/**
	 * When the session gets destroyed, remove this listener (preventing memory-leaks and degrading performance due to stale listeners).
	 */
	private void removeWhenSessionDestroyed(EventListener<EVENT_DATA> listener, SessionContext currentSessionContext) {
		if (this != currentSessionContext.onDestroyed()) { // prevent infinite recursion!
			// use a weak reference here, so the fact that this is registered to the sessionContext's destroyed event
			// does not mean it has to survive (not being garbage collected) as long as the session context.
			WeakReference<EventListener<EVENT_DATA>> listenerWeakReference = new WeakReference<>(listener);
			currentSessionContext.onDestroyed().listeners.add(aVoid -> {
				EventListener<EVENT_DATA> l = listenerWeakReference.get();
				if (l != null) {
					removeListener(l);
				}
			});
		}
	}

	public void removeListener(EventListener<EVENT_DATA> listener) {
		listeners.remove(new SessionContextAwareEventListener<>(listener));
	}

	public void fire(EVENT_DATA eventData) {
		this.lastEventData = eventData;
		for (EventListener<EVENT_DATA> listener : listeners) {
			listener.onEvent(eventData);
		}
	}

	public void fire() {
		fire(null);
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

	private static class SessionContextAwareEventListener<EVENT_DATA> implements EventListener<EVENT_DATA> {

		private final SessionContext sessionContext;
		private final EventListener<EVENT_DATA> delegate;

		public SessionContextAwareEventListener(SessionContext sessionContext, EventListener<EVENT_DATA> delegate) {
			this.sessionContext = sessionContext;
			this.delegate = delegate;
		}

		public SessionContextAwareEventListener(EventListener<EVENT_DATA> delegate) {
			this(null, delegate);
		}

		@Override
		public void onEvent(EVENT_DATA eventData) {
			if (sessionContext != null) {
				sessionContext.runWithContext(() -> delegate.onEvent(eventData));
			} else {
				delegate.onEvent(eventData);
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			SessionContextAwareEventListener<?> that = (SessionContextAwareEventListener<?>) o;

			return delegate != null ? delegate.equals(that.delegate) : that.delegate == null;

		}

		@Override
		public int hashCode() {
			return delegate != null ? delegate.hashCode() : 0;
		}
	}

}
