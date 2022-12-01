package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;

public class DtoINITWrapper extends DtoAbstractClientMessageWrapper {

	public DtoINITWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public String getSessionId() {
		return jsonNode.get("sessionId").textValue();
	}

	public DtoClientInfoWrapper getClientInfo() {
		var node = jsonNode.get("clientInfo");
		if (node == null || node.isNull()) {
			return null;
		}
		if (!node.isObject()) {
			throw new IllegalArgumentException("node must be an object!");
		}
		return new DtoClientInfoWrapper(node);
	}

	public int getMaxRequestedCommandId() {
		return jsonNode.get("maxRequestedCommandId").asInt();
	}

}