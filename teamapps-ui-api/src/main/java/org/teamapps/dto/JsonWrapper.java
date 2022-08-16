package org.teamapps.dto;

import com.fasterxml.jackson.databind.JsonNode;

class JsonWrapper {
	JsonNode jsonNode;

	public JsonWrapper(JsonNode jsonNode) {
		this.jsonNode = jsonNode;
	}

	public JsonNode getJsonNode() {
		return jsonNode;
	}

	public <W extends JsonWrapper> W as(Class<W> clazz) {
		try {
			return clazz.getConstructor(JsonNode.class).newInstance(jsonNode);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}