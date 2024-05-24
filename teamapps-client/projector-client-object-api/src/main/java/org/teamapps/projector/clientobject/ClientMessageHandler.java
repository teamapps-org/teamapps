package org.teamapps.projector.clientobject;

import org.teamapps.projector.dto.JsonWrapper;

import java.util.List;

public interface ClientMessageHandler {

	void handleEvent(String name, List<JsonWrapper> params);

	default Object handleQuery(String name, List<JsonWrapper> params) {
		return null;
	}
	
}
