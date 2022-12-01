package org.teamapps.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class DtoJsonWrapper {

	protected final JsonNode jsonNode;

	public DtoJsonWrapper(JsonNode jsonNode) {
		this.jsonNode = jsonNode;
	}

	public JsonNode getJsonNode() {
		return jsonNode;
	}

	public String getTypeId() {
		return jsonNode.get("_type").textValue();
	}

	public <W extends DtoJsonWrapper> W as(Class<W> clazz) {
		try {
			return clazz.getConstructor(JsonNode.class).newInstance(jsonNode);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}