package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.projector.dto.JsonWrapper;

import java.lang.invoke.MethodHandles;

public class EVTWrapper extends AbstractReliableClientMessageWrapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static final String TYPE_ID = "EVT";

	public EVTWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public String getLibraryId() {
		return jsonNode.get("lid").textValue();
	}

	public String getClientObjectId() {
		return jsonNode.get("oid").textValue();
	}

	public String getName() {
		return jsonNode.get("name").textValue();
	}

	public JsonWrapper getEventObject() {
		var node = jsonNode.get("evtObj");
		if (node == null || node.isNull()) {
			return null;
		}
		return new JsonWrapper(node);
	}

}