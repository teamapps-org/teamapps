package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;



public abstract class DtoAbstractClientPayloadMessage extends DtoAbstractClientMessage {

	protected final int id;

	@JsonCreator
	public DtoAbstractClientPayloadMessage(@JsonProperty("sessionId") String sessionId, @JsonProperty("id") int id) {
		super(sessionId);
		this.id = id;
	}

	public int getId() {
		return id;
	}

}