package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.JsonWrapper;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class EVTWrapper extends AbstractReliableClientMessageWrapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static final String TYPE_ID = "EVT";

	public EVTWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public String getLibraryId() {
		return jsonNode.get("lid").asText();
	}

	public String getClientObjectId() {
		return jsonNode.get("oid").asText();
	}

	public String getName() {
		return jsonNode.get("name").asText();
	}

	public List<JsonWrapper> getParams() {
		var node = jsonNode.get("params");
		if (node == null || node.isNull()) {
			return List.of();
		}
		if (!node.isArray()) {
			LOGGER.warn("Event '{}' has params property that is not a list!", getName());
			return List.of(new JsonWrapper(node));
		}
		//noinspection UnstableApiUsage
		return Streams.stream(node.elements())
				.map(JsonWrapper::new)
				.toList();
	}

	public boolean isAwaitingResult() {
		JsonNode r = jsonNode.get("r");
		return r != null && r.asBoolean();
	}
}