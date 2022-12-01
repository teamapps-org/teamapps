package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DtoCMD_RES extends DtoAbstractClientPayloadMessage {

	public static final String TYPE_ID = "CMD_RES";

	protected final int cmdId;
	protected final Object result;

	@JsonCreator
	public DtoCMD_RES(@JsonProperty("sessionId") String sessionId, @JsonProperty("id") int id, @JsonProperty("cmdId") int cmdId, @JsonProperty("result") Object result) {
		super(sessionId, id);
		this.cmdId = cmdId;
		this.result = result;
	}

	public int getCmdId() {
		return cmdId;
	}

	public Object getResult() {
		return result;
	}

}