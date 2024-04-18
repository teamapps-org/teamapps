package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.dto.JsonWrapper;


public class AbstractReliableClientMessageWrapper extends JsonWrapper {

	public AbstractReliableClientMessageWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public int getSequenceNumber() {
		return jsonNode.get("sn").asInt();
	}

}