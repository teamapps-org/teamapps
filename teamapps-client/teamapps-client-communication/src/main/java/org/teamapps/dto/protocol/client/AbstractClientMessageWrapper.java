package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.teamapps.projector.dto.JsonWrapper;


public class AbstractClientMessageWrapper extends JsonWrapper {

	public AbstractClientMessageWrapper(ObjectMapper objectMapper, JsonNode jsonNode) {
		super(objectMapper, jsonNode);
	}

}