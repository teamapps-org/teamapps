package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;

public class REINITWrapper extends AbstractClientMessageWrapper {

	public static final String TYPE_ID = "REINIT";

	public REINITWrapper(JsonNode jsonNode) {
		super(jsonNode);
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