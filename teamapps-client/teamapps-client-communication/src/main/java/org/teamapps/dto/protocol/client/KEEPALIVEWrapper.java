package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;

public class KEEPALIVEWrapper extends AbstractClientMessageWrapper {

	public static final String TYPE_ID = "KEEPALIVE";

	public KEEPALIVEWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

}