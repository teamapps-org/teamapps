package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("REINIT")
public class DtoREINIT extends DtoAbstractClientMessage {

	public static final String TYPE_ID = "REINIT";

	protected final int lastReceivedCommandId;
	protected final int maxRequestedCommandId;

	@JsonCreator
	public DtoREINIT(@JsonProperty("sessionId") String sessionId, @JsonProperty("lastReceivedCommandId") int lastReceivedCommandId, @JsonProperty("maxRequestedCommandId") int maxRequestedCommandId) {
		super(sessionId);
		this.lastReceivedCommandId = lastReceivedCommandId;
		this.maxRequestedCommandId = maxRequestedCommandId;
	}

	public int getLastReceivedCommandId() {
		return lastReceivedCommandId;
	}

	public int getMaxRequestedCommandId() {
		return maxRequestedCommandId;
	}

}