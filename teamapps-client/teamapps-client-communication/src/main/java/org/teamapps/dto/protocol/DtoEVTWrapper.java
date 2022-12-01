package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;

public class DtoEVTWrapper extends DtoAbstractClientPayloadMessageWrapper {

	public DtoEVTWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public DtoEventWrapper getUiEvent() {
		var node = jsonNode.get("uiEvent");
		if (node == null || node.isNull()) {
			return null;
		}
		if (!node.isObject()) {
			throw new IllegalArgumentException("node must be an object!");
		}
		return new DtoEventWrapper(node);
	}

}