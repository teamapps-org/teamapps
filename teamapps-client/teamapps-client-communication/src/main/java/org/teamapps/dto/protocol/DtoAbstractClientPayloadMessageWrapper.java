package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;



public class DtoAbstractClientPayloadMessageWrapper extends DtoAbstractClientMessageWrapper {

	public DtoAbstractClientPayloadMessageWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public int getId() {
		return jsonNode.get("id").asInt();
	}

}