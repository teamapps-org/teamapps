package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class REINITWrapper extends AbstractClientMessageWrapper {

	public static final String TYPE_ID = "REINIT";

	public REINITWrapper(ObjectMapper objectMapper, JsonNode jsonNode) {
		super(objectMapper, jsonNode);
	}

	public String getSessionId() {
		return jsonNode.get("sessionId").textValue();
	}

	public int getLastReceivedCommandId() {
		return jsonNode.get("lastReceivedCommandId").asInt();
	}


	public int getMaxRequestedCommandId() {
		return jsonNode.get("maxRequestedCommandId").asInt();
	}

}