package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class TERMINATEWrapper extends AbstractClientMessageWrapper {

	public static final String TYPE_ID = "TERMINATE";

	public TERMINATEWrapper(ObjectMapper objectMapper, JsonNode jsonNode) {
		super(objectMapper, jsonNode);
	}

}