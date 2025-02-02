package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;

public class INITWrapper extends AbstractClientMessageWrapper {

	public static final String TYPE_ID = "INIT";

	public INITWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public String getSessionId() {
		return jsonNode.get("sessionId").textValue();
	}

	public ClientInfoWrapper getClientInfo() {
		var node = jsonNode.get("clientInfo");
		if (node == null || node.isNull()) {
			return null;
		}
		if (!node.isObject()) {
			throw new IllegalArgumentException("node must be an object!");
		}
		return new ClientInfoWrapper(node);
	}

	public int getMaxRequestedCommandId() {
		return jsonNode.get("maxRequestedCommandId").asInt();
	}

}