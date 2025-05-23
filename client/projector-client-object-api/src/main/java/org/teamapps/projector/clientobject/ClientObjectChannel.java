package org.teamapps.projector.clientobject;

import org.teamapps.commons.event.Disposable;
import org.teamapps.projector.dto.JsonWrapper;

import java.util.function.Consumer;

/**
 * Represents a communication channel to a client-side of a {@link ClientObject}.
 * <p>
 * This interface provides methods to interact with client-side objects, including:
 * <ul>
 *   <li>Forcing rendering of the client object</li>
 *   <li>Checking if the client object is rendered</li>
 *   <li>Sending commands to the client object</li>
 *   <li>Managing event subscriptions</li>
 *   <li>Registering client-side event handlers</li>
 * </ul>
 */
public interface ClientObjectChannel {

	/**
	 * Forces the client object to be rendered on the client side.
	 * <p>
	 * This method triggers the rendering process for the client object
	 * if it has not been rendered yet.
	 */
	void forceRender();

	/**
	 * Checks if the client object is currently rendered on the client side.
	 *
	 * @return {@code true} if the client object is rendered, {@code false} otherwise
	 */
	boolean isRendered();

	/**
	 * Sends a command to the client object if it is rendered, without expecting a result.
	 * <p>
	 * This is a convenience method that calls {@link #sendCommandIfRendered(String, Object[], Consumer)}
	 * with a null result handler.
	 *
	 * @param name the name of the command to send
	 * @param params the parameters to pass with the command
	 * @return {@code true} if the command was sent (client object is rendered),
	 *         {@code false} if the client object is not yet rendered
	 */
	default boolean sendCommandIfRendered(String name, Object... params) {
		return sendCommandIfRendered(name, params, null);
	}

	/**
	 * Sends a command to the client object if it is rendered, with the option to handle the result.
	 * <p>
	 * This method sends a command to the client-side object only if it has been rendered.
	 * If the client object is not yet rendered, the command is not sent and the method returns {@code false}.
	 *
	 * @param name the name of the command to send
	 * @param params the parameters to pass with the command
	 * @param resultHandler a consumer that will be called with the result of the command execution,
	 *                     or {@code null} if no result handling is needed
	 * @return {@code true} if the command was sent (client object is rendered),
	 *         {@code false} if the client object is not yet rendered
	 */
	boolean sendCommandIfRendered(String name, Object[] params, Consumer<JsonWrapper> resultHandler);

	/**
	 * Enables or disables a specific event on the client side.
	 * <p>
	 * This method controls whether the client should send events with the specified name
	 * to the server. This can be used to optimize performance by only enabling events
	 * that are currently needed.
	 *
	 * @param eventName the name of the event to toggle
	 * @param enabled {@code true} to enable the event, {@code false} to disable it
	 */
	void toggleEvent(String eventName, boolean enabled);

	/**
	 * Registers a client-side event handler. This handler will be called on the client-side (without server-round-trip)
	 * to handle the event.
	 *
	 * @param eventName the name of the event that should be handled
	 * @param invokable the {@link InvokableClientObject} that should be invoked on the client side when the event occurs
	 *                  (see {@code Invokable.ts})
	 * @param functionName the function name that should be invoked
	 * @param eventObjectAsFirstParameter {@code true} to use the client-side event object as first function parameter,
	 *                                   {@code false} otherwise
	 * @param params the parameters to use for the invocation
	 * @return a disposable for unregistering this handler
	 */
	Disposable addClientSideEventHandler(String eventName, InvokableClientObject invokable, String functionName,
	                                     boolean eventObjectAsFirstParameter, Object... params);

}
