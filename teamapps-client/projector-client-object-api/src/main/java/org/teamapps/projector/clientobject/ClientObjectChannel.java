package org.teamapps.projector.clientobject;

import org.teamapps.projector.dto.JsonWrapper;

import java.util.function.Consumer;

public interface ClientObjectChannel {

	void forceRender();

	default void sendCommandIfRendered(String name, Object... params) {
		sendCommandIfRendered(name, params, null);
	}
	
	void sendCommandIfRendered(String name, Object[] params, Consumer<JsonWrapper> resultHandler);

	void toggleEvent(String eventName, boolean enabled);

}
