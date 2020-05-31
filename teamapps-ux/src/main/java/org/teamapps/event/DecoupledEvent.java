package org.teamapps.event;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Forces events to be handled in a different Thread than the firing one.
 */
public class DecoupledEvent<EVENT_DATA> extends Event<EVENT_DATA> {

	private final Executor executor;

	public DecoupledEvent(Executor executor) {
		this.executor = executor;
	}

	@Override
	protected void invokeListener(EVENT_DATA eventData, Consumer<EVENT_DATA> listener) {
		executor.execute(() -> super.invokeListener(eventData, listener));
	}
}
