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
package org.teamapps.projector.event;

import org.teamapps.commons.event.Disposable;
import org.teamapps.commons.event.Event;
import org.teamapps.commons.event.EventListenerRegistrationStatusListener;
import org.teamapps.commons.event.SelfDisposingEventListener;
import org.teamapps.projector.session.CurrentSessionContext;
import org.teamapps.projector.session.SessionContext;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Represents an event that can get fired.
 * <p>
 * Listeners can be added to this event using the various {@link #addListener(Consumer) addListener(...)} methods.
 * <p>
 * <h2>SessionContext-bound Event Listeners</h2>
 * Note that if a listener <code>L</code> is added while the thread is bound to a {@link SessionContext} <code>A</code> (so while {@link SessionContext#currentOrNull()} is not null),
 * <code>L</code>'s execution will be bound to <code>A</code> by default, i.e.:
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
public class ProjectorEvent<EVENT_DATA> extends Event<EVENT_DATA> {

	public ProjectorEvent() {
		super();
	}

    /**
     * Creates a new ProjectorEvent with a custom listener registration status handler.
     * <p>
     * This allows custom behavior when listeners are registered or unregistered.
     *
     * @param listenerHandler The handler to be notified when listeners are registered or unregistered
     */
	public ProjectorEvent(EventListenerRegistrationStatusListener listenerHandler) {
		super(listenerHandler);
	}

    /**
     * Adds a listener to this event, binding it to the current session context if one exists.
     * <p>
     * This is equivalent to {@code addListener(listener, true)}.
     *
     * @param listener The listener to be added
     * @return A Disposable that can be used to remove the listener
     */
	@Override
	public Disposable addListener(Consumer<EVENT_DATA> listener) {
		return addListener(listener, true);
	}

    /**
     * Adds a listener to this event with optional session context binding.
     * <p>
     * If bindToSessionContext is true and a session context is currently active,
     * the listener will be bound to that session context.
     *
     * @param listener The listener to be added
     * @param bindToSessionContext Whether to bind the listener to the current session context
     * @return A Disposable that can be used to remove the listener
     */
	public Disposable addListener(Consumer<EVENT_DATA> listener, boolean bindToSessionContext) {
		SessionContext sessionContextToBindTo = bindToSessionContext ? CurrentSessionContext.getOrNull() : null;

		if (sessionContextToBindTo != null) {
			listener = new SessionContextAwareEventListener<>(sessionContextToBindTo, listener);
		}

		Disposable disposable = super.addListener(listener);

		Disposable sessionDestroyedDisposable = sessionContextToBindTo != null ? removeWhenSessionDestroyed(listener, sessionContextToBindTo) : null;

		return () -> {
			disposable.dispose();
			if (sessionDestroyedDisposable != null) {
				sessionDestroyedDisposable.dispose();
			}
		};
	}

    /**
     * Adds a self-disposing listener to this event, binding it to the current session context if one exists.
     * <p>
     * This is equivalent to {@code addListener(listener, true)}.
     *
     * @param listener The self-disposing listener to be added
     * @return A Disposable that can be used to remove the listener
     */
	@Override
	public Disposable addListener(SelfDisposingEventListener<EVENT_DATA> listener) {
		return addListener(listener, true);
	}

    /**
     * Adds a self-disposing listener to this event with optional session context binding.
     * <p>
     * A self-disposing listener receives a Disposable in its handle method that it can use to remove itself.
     *
     * @param listener The self-disposing listener to be added
     * @param bindToSessionContext Whether to bind the listener to the current session context
     * @return A Disposable that can be used to remove the listener
     */
	public Disposable addListener(SelfDisposingEventListener<EVENT_DATA> listener, boolean bindToSessionContext) {
		AtomicReference<Disposable> disposable = new AtomicReference<>();
		disposable.set(addListener(e -> {
			listener.handle(e, disposable.get());
		}, bindToSessionContext));
		return disposable.get();
	}

    /**
     * Adds a Runnable as a listener to this event, binding it to the current session context if one exists.
     * <p>
     * This is equivalent to {@code addListener(listener, true)}.
     *
     * @param listener The Runnable to be added as a listener
     * @return A Disposable that can be used to remove the listener
     */
	@Override
	public Disposable addListener(Runnable listener) {
		return addListener(listener, true);
	}

    /**
     * Adds a Runnable as a listener to this event with optional session context binding.
     * <p>
     * The Runnable will be executed when the event is fired, ignoring the event data.
     *
     * @param listener The Runnable to be added as a listener
     * @param bindToSessionContext Whether to bind the listener to the current session context
     * @return A Disposable that can be used to remove the listener
     */
	public Disposable addListener(Runnable listener, boolean bindToSessionContext) {
		return addListener(new RunnableWrapper<>(listener), bindToSessionContext);
	}

	/**
	 * When the session gets destroyed, remove this listener (preventing memory-leaks and degrading performance due to stale listeners).
	 *
	 * @param listener The listener to be removed when the session context is destroyed
	 * @param currentSessionContext The session context that, when destroyed, should trigger listener removal
	 * @return A Disposable that can be used to cancel this auto-removal behavior
	 */
	private Disposable removeWhenSessionDestroyed(Consumer<EVENT_DATA> listener, SessionContext currentSessionContext) {
		// use a weak reference here, so the fact that this is registered to the sessionContext's destroyed event
		// does not mean it has to survive (not being garbage collected) as long as the session context.
		WeakReference<Consumer<EVENT_DATA>> listenerWeakReference = new WeakReference<>(listener);
		return currentSessionContext.onDestroyed().addListener(aVoid -> {
			Consumer<EVENT_DATA> l = listenerWeakReference.get();
			if (l != null) {
				listeners.remove(l);
			}
		});
	}

    /**
     * An event listener implementation that ensures a listener is executed within a specific session context.
     * <p>
     * This wrapper ensures that when the event is fired, the delegate listener is executed
     * within the session context it was bound to, regardless of the current thread's session context.
     *
     * @param <EVENT_DATA> The type of data the event fires
     */
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

}
