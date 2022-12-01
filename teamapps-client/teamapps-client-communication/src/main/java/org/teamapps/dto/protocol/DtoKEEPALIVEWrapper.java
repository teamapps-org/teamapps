package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;

public class DtoKEEPALIVEWrapper extends DtoAbstractClientMessageWrapper {

	public static final String TYPE_ID = "KEEPALIVE";

	public DtoKEEPALIVEWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

}