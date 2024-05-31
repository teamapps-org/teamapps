package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;


public class REQNWrapper extends AbstractClientMessageWrapper {

	public static final String TYPE_ID = "REQN";

	public REQNWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public int getLastReceivedCommandId() {
		return jsonNode.get("lastReceivedCommandId").asInt();
	}

	public int getMaxRequestedCommandId() {
		return jsonNode.get("maxRequestedCommandId").asInt();
	}

}