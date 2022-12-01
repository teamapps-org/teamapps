package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.teamapps.dto.DtoEvent;

@JsonTypeName("EVT")
public class DtoEVT extends DtoAbstractClientPayloadMessage {

	public static final String TYPE_ID = "EVT";

	protected final DtoEvent uiEvent;

	@JsonCreator
	public DtoEVT(@JsonProperty("sessionId") String sessionId, @JsonProperty("id") int id, @JsonProperty("uiEvent") DtoEvent uiEvent) {
		super(sessionId, id);
		this.uiEvent = uiEvent;
	}

	public DtoEvent getUiEvent() {
		return uiEvent;
	}

}