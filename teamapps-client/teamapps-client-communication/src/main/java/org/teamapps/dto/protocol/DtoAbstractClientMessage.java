package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
public abstract class DtoAbstractClientMessage {

	protected final String sessionId; // TODO check if we still need this!

	@JsonCreator
	public DtoAbstractClientMessage(@JsonProperty("sessionId") String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

}