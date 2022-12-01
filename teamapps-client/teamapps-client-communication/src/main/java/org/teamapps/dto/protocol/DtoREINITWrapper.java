package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;

public class DtoREINITWrapper extends DtoAbstractClientMessageWrapper {

	public DtoREINITWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public Class<? extends DtoREINIT> getDtoClass() {
		return DtoREINIT.class;
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