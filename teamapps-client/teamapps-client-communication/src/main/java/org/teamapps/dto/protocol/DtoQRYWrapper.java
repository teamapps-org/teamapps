package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;

public class DtoQRYWrapper extends DtoAbstractClientPayloadMessageWrapper {

	public static final String TYPE_ID = "QRY";

	public DtoQRYWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public DtoQueryWrapper getUiQuery() {
		var node = jsonNode.get("uiQuery");
		if (node == null || node.isNull()) {
			return null;
		}
		if (!node.isObject()) {
			throw new IllegalArgumentException("node must be an object!");
		}
		return new DtoQueryWrapper(node);

	}

}