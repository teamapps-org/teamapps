package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class INITWrapper extends AbstractClientMessageWrapper {

	public static final String TYPE_ID = "INIT";

	public INITWrapper(ObjectMapper objectMapper, JsonNode jsonNode) {
		super(objectMapper, jsonNode);
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
		return new ClientInfoWrapper(getObjectMapper(), node);
	}

	public int getMaxRequestedCommandId() {
		return jsonNode.get("maxRequestedCommandId").asInt();
	}

}