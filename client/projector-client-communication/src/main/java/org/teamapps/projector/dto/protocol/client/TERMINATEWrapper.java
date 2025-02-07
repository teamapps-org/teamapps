package org.teamapps.projector.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;


public class TERMINATEWrapper extends AbstractClientMessageWrapper {

	public static final String TYPE_ID = "TERMINATE";

	public TERMINATEWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

}