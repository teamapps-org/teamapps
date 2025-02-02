package org.teamapps.projector.clientobject;

import org.teamapps.projector.dto.JsonWrapper;

import java.util.function.Consumer;

public interface ClientObjectChannel {

	void forceRender();

	boolean isRendered();

	default boolean sendCommandIfRendered(String name, Object... params) {
		return sendCommandIfRendered(name, params, null);
	}

	/**
	 *
	 * @param name
	 * @param params
	 * @param resultHandler
	 * @return true if the command was actually sent, false if the client object is no yet rendered
	 */
	boolean sendCommandIfRendered(String name, Object[] params, Consumer<JsonWrapper> resultHandler);

	void toggleEvent(String eventName, boolean enabled);

}
