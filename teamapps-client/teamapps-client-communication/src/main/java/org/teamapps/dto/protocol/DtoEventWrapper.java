package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.dto.DtoEvent;


public class DtoEventWrapper extends DtoAbstractClientMessageWrapper {

	public DtoEventWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public Class<? extends DtoEvent> getDtoClass() {
		return DtoEvent.class;
	}

	public String getComponentId() {
		var node = jsonNode.get("componentId");
		if (node == null || node.isNull()) {
			return null;
		}
		return node.textValue();

	}

}