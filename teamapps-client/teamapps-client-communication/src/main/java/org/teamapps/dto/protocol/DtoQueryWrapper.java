package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.dto.DtoJsonWrapper;

public class DtoQueryWrapper extends DtoJsonWrapper {

	public DtoQueryWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public String getComponentId() {
		var node = jsonNode.get("componentId");
		if (node == null || node.isNull()) {
			return null;
		}
		return node.textValue();

	}

}