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

import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

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

	public ProjectorEvent(EventListenerRegistrationListener listenerHandler) {
		super(listenerHandler);
	}

	@Override
	public Disposable addListener(Consumer<EVENT_DATA> listener) {
		return addListener(listener, true);
	}

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

	@Override
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

	@Override
	public Disposable addListener(Runnable listener) {
		return addListener(listener, true);
	}

	public Disposable addListener(Runnable listener, boolean bindToSessionContext) {
		return addListener(new RunnableWrapper<>(listener), bindToSessionContext);
	}

	/**
	 * When the session gets destroyed, remove this listener (preventing memory-leaks and degrading performance due to stale listeners).
	 *
	 * @return
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
