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
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

import java.lang.ref.WeakReference;
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
 * <p>
 * <h2>SessionContext-bound Event Listeners</h2>
 * Note that if a listener is added while the thread is bound to a {@link SessionContext} <code>A</code> (so while {@link SessionContext#currentOrNull()} is not null),
 * the event will be bound to this SessionContext (<code>A</code>) by default, i.e.:
 * <ul>
 *     <li>When this event fires, the SessionContext-bound listener will get invoked bound to the SessionContext <code>A</code>,
 *     regardless of the SessionContext (or the lack of it) the event was fired in.</li>
 *     <li>When the SessionContext <code>A</code> is destroyed (and thereby fires its {@link SessionContext#onDestroyed} event), the listener is automatically detached from this event.</li>
 * </ul>
 * You can prevent a listener from being bound to the current SessionContext,
 * by using one of the {@link #addListener(Consumer, boolean) addListener(..., boolean bindToSessionContext)} methods.
 *
 * @param <EVENT_DATA> The type of data this event fires.
 */
public class Event<EVENT_DATA> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Event.class);
	private final String source; // for debugging

	private final List<Consumer<EVENT_DATA>> listeners = new CopyOnWriteArrayList<>();
	private EVENT_DATA lastEventData;

	public Event() {
		StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
		this.source = stackTraceElement.getFileName() + stackTraceElement.getLineNumber();
	}

	public Disposable addListener(Consumer<EVENT_DATA> listener) {
		return addListener(listener, true);
	}

	public Disposable addListener(Consumer<EVENT_DATA> listener, boolean bindToSessionContext) {
		SessionContext currentSessionContext;
		if (bindToSessionContext && (currentSessionContext = CurrentSessionContext.getOrNull()) != null) {
			listeners.add(new SessionContextAwareEventListener<>(currentSessionContext, listener));
			removeWhenSessionDestroyed(listener, currentSessionContext);
		} else {
			// just add the listener. It will get called with whatever context is active at firing time
			listeners.add(listener);
		}
		return () -> removeListener(listener);
	}

	public Disposable addListener(SelfDisposingEventListener<EVENT_DATA> listener) {
		return addListener(listener, true);
	}

	public Disposable addListener(SelfDisposingEventListener<EVENT_DATA> listener, boolean bindToSessionContext) {
		AtomicReference<Disposable> disposable = new AtomicReference<>();
		disposable.set(addListener(e -> {
			listener.handle(e, disposable.get());
		}, bindToSessionContext));
		return disposable.get();
	}

	public Disposable addListener(Runnable listener) {
		return addListener(listener, true);
	}

	public Disposable addListener(Runnable listener, boolean bindToSessionContext) {
		return addListener(new RunnableWrapper<>(listener), bindToSessionContext);
	}

	List<Consumer<EVENT_DATA>> getListeners() {
		return listeners;
	}

	/**
	 * @deprecated Use the {@link Disposable} returned by {@link #addListener(Runnable)} instead!
	 */
	@Deprecated
	public void removeListener(Runnable listener) {
		removeListener(new RunnableWrapper<>(listener));
	}

	/**
	 * @deprecated Use the {@link Disposable} returned by {@link #addListener(Consumer)} instead!
	 */
	@Deprecated
	public void removeListener(Consumer<EVENT_DATA> listener) {
		listeners.remove(listener); // in case it is not bound to a session
		listeners.remove(new SessionContextAwareEventListener<>(listener));
	}

	/**
	 * When the session gets destroyed, remove this listener (preventing memory-leaks and degrading performance due to stale listeners).
	 */
	private void removeWhenSessionDestroyed(Consumer<EVENT_DATA> listener, SessionContext currentSessionContext) {
		if (this != currentSessionContext.onDestroyed) { // prevent infinite recursion!
			// use a weak reference here, so the fact that this is registered to the sessionContext's destroyed event
			// does not mean it has to survive (not being garbage collected) as long as the session context.
			WeakReference<Consumer<EVENT_DATA>> listenerWeakReference = new WeakReference<>(listener);
			currentSessionContext.onDestroyed.listeners.add(aVoid -> {
				Consumer<EVENT_DATA> l = listenerWeakReference.get();
				if (l != null) {
					removeListener(l);
				}
			});
		}
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

	private static class SessionContextAwareEventListener<EVENT_DATA> implements Consumer<EVENT_DATA> {

		private final SessionContext sessionContext;
		private final Consumer<EVENT_DATA> delegate;

		public SessionContextAwareEventListener(SessionContext sessionContext, Consumer<EVENT_DATA> delegate) {
			this.sessionContext = sessionContext;
			this.delegate = delegate;
		}

		public SessionContextAwareEventListener(Consumer<EVENT_DATA> delegate) {
			this(null, delegate);
		}

		@Override
		public void accept(EVENT_DATA eventData) {
			if (sessionContext != null) {
				sessionContext.runWithContext(() -> delegate.accept(eventData));
			} else {
				delegate.accept(eventData);
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

	private static class RunnableWrapper<T> implements Consumer<T> {

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
