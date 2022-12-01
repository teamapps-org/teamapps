package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("REINIT_OK")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoREINIT_OK extends DtoAbstractServerMessage {

	public static final String TYPE_ID = "REINIT_OK";

	protected final int lastReceivedEventId;

	@JsonCreator
	public DtoREINIT_OK(@JsonProperty("lastReceivedEventId") int lastReceivedEventId) {
		super();
		this.lastReceivedEventId = lastReceivedEventId;
	}

	public int getLastReceivedEventId() {
		return lastReceivedEventId;
	}

}