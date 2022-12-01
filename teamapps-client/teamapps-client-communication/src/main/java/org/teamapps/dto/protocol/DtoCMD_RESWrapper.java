package org.teamapps.dto.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.dto.DtoJsonWrapper;


public class DtoCMD_RESWrapper extends DtoAbstractClientPayloadMessageWrapper {

	public DtoCMD_RESWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public Class<? extends DtoCMD_RES> getDtoClass() {
		return DtoCMD_RES.class;
	}

	public int getCmdId() {
		return jsonNode.get("cmdId").asInt();

	}

	public DtoJsonWrapper getResult() {
		return new DtoJsonWrapper(jsonNode.get("result"));

	}

}