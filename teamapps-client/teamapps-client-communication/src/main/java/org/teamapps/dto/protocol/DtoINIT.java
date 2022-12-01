package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("INIT")
public class DtoINIT extends DtoAbstractClientMessage {

	public static final String TYPE_ID = "INIT";

	protected final DtoClientInfo clientInfo;
	protected final int maxRequestedCommandId;

	@JsonCreator
	public DtoINIT(@JsonProperty("sessionId") String sessionId, @JsonProperty("clientInfo") DtoClientInfo clientInfo, @JsonProperty("maxRequestedCommandId") int maxRequestedCommandId) {
		super(sessionId);
		this.clientInfo = clientInfo;
		this.maxRequestedCommandId = maxRequestedCommandId;
	}

	public DtoClientInfo getClientInfo() {
		return clientInfo;
	}

	public int getMaxRequestedCommandId() {
		return maxRequestedCommandId;
	}

}