package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.dto.DtoJsonWrapper;


public class DtoAbstractClientMessageWrapper extends DtoJsonWrapper {

	public DtoAbstractClientMessageWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

}