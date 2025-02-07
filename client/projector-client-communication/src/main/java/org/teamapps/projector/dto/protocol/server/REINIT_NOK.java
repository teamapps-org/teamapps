package org.teamapps.projector.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;


@JsonTypeName("REINIT_NOK")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class REINIT_NOK extends AbstractServerMessage {

	@JsonProperty("reason")
	protected final SessionClosingReason reason;

	@JsonCreator
	public REINIT_NOK(@JsonProperty("reason") SessionClosingReason reason) {
		super();
		this.reason = reason;
	}

	public SessionClosingReason getReason() {
		return reason;
	}

}