package org.teamapps.projector.clientobject;

import org.teamapps.projector.dto.JsonWrapper;

import java.util.List;

/**
 * An interface for handling messages from the client side.
 * Implementations of this interface can process events and respond to queries
 * that are sent from the client to the server.
 */
public interface ClientMessageHandler {

	/**
	 * Handles an event sent from the client.
	 *
	 * @param name The name of the event
	 * @param eventObject The data associated with the event, wrapped in a JsonWrapper
	 */
	void handleEvent(String name, JsonWrapper eventObject);

	/**
	 * Handles a query sent from the client and returns a response.
	 *
	 * @param name The name of the query
	 * @param params The parameters for the query, wrapped in JsonWrapper objects
	 * @return The response to the query
	 */
	default Object handleQuery(String name, List<JsonWrapper> params) {
		return null;
	}

}
