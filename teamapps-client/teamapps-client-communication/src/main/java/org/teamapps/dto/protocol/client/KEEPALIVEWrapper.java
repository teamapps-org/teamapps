package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KEEPALIVEWrapper extends AbstractClientMessageWrapper {

	public static final String TYPE_ID = "KEEPALIVE";

	public KEEPALIVEWrapper(ObjectMapper objectMapper, JsonNode jsonNode) {
		super(objectMapper, jsonNode);
	}

}