package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;

public class DtoKEEPALIVEWrapper extends DtoAbstractClientMessageWrapper {

	public DtoKEEPALIVEWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public Class<? extends DtoKEEPALIVE> getDtoClass() {
		return DtoKEEPALIVE.class;
	}

}