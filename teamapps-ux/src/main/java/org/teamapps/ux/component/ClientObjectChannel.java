package org.teamapps.ux.component;

import org.teamapps.dto.JsonWrapper;

import java.util.function.Consumer;

public interface ClientObjectChannel {

	default void sendCommand(String name, Object... params) {
		sendCommand(name, params, null);
	}
	
	void sendCommand(String name, Object[] params, Consumer<JsonWrapper> resultHandler);

	void toggleEvent(String eventName, boolean enabled);

}
