package org.teamapps.projector.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("REINIT_OK")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class REINIT_OK extends AbstractServerMessage {

	public static final String TYPE_ID = "REINIT_OK";

	@JsonProperty("lastReceivedEventId")
	protected final int lastReceivedEventId;

	@JsonCreator
	public REINIT_OK(@JsonProperty("lastReceivedEventId") int lastReceivedEventId) {
		super();
		this.lastReceivedEventId = lastReceivedEventId;
	}

	public int getLastReceivedEventId() {
		return lastReceivedEventId;
	}

}