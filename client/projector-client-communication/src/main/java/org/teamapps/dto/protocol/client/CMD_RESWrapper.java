package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.projector.dto.JsonWrapper;


public class CMD_RESWrapper extends AbstractReliableClientMessageWrapper {

	public static final String TYPE_ID = "CMD_RES";

	public CMD_RESWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public int getCmdSn() {
		return jsonNode.get("cmdSn").asInt();
	}

	public JsonWrapper getResult() {
		var node = jsonNode.get("result");
		if (node == null || node.isNull()) {
			return null;
		}
		return new JsonWrapper(node);
	}

}