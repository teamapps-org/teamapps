package org.teamapps.projector.clientobject;

import org.teamapps.commons.event.Disposable;
import org.teamapps.projector.dto.JsonWrapper;

import java.util.function.Consumer;

public interface ClientObjectChannel {

	void forceRender();

	boolean isRendered();

	default boolean sendCommandIfRendered(String name, Object... params) {
		return sendCommandIfRendered(name, params, null);
	}

	/**
	 * @param name
	 * @param params
	 * @param resultHandler
	 * @return true if the command was actually sent, false if the client object is no yet rendered
	 */
	boolean sendCommandIfRendered(String name, Object[] params, Consumer<JsonWrapper> resultHandler);

	void toggleEvent(String eventName, boolean enabled);

	/**
	 * Registers a client-side event handler. This handler will be called on the client-side (without server-round-trip)
	 * to handle the event.
	 * @param eventName the name of the event that should be handled
	 * @param invokable the {@link ClientSideInvokable} that should be invoked on the client side when the event occurs.
	 * @param functionName the function name that should be invoked
	 * @param eventObjectAsFirstParameter use the client-side event object as first function parameter
	 * @param params the parameters to use for the invocation
	 * @return a disposable for unregistering this handler
	 */
	Disposable addClientSideEventHandler(String eventName, ClientSideInvokable invokable, String functionName,
	                                     boolean eventObjectAsFirstParameter, Object... params);

}
