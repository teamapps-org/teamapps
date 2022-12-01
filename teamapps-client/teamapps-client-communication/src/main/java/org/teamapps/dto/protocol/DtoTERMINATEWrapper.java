package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;



public class DtoTERMINATEWrapper extends DtoAbstractClientMessageWrapper {

	public static final String TYPE_ID = "TERMINATE";

	public DtoTERMINATEWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public Class<? extends DtoTERMINATE> getDtoClass() {
		return DtoTERMINATE.class;
	}

}