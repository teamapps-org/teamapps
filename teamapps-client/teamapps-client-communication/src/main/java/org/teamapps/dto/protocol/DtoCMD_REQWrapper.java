package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;



public class DtoCMD_REQWrapper extends DtoAbstractClientMessageWrapper {

	public DtoCMD_REQWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public int getLastReceivedCommandId() {
		return jsonNode.get("lastReceivedCommandId").asInt();
	}

	public int getMaxRequestedCommandId() {
		return jsonNode.get("maxRequestedCommandId").asInt();
	}

}