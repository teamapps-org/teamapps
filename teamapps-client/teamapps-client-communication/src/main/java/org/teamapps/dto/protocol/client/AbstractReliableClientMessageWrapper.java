package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.teamapps.projector.dto.JsonWrapper;


public class AbstractReliableClientMessageWrapper extends JsonWrapper {

	public AbstractReliableClientMessageWrapper(ObjectMapper objectMapper, JsonNode jsonNode) {
		super(objectMapper, jsonNode);
	}

	public int getSequenceNumber() {
		return jsonNode.get("sn").asInt();
	}

}