package org.teamapps.projector.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("QUERY_RES")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QUERY_RES extends AbstractReliableServerMessage {

	@JsonProperty("evtId")
	protected final int evtId;
	@JsonProperty("result")
	protected final Object result;

	@JsonCreator
	public QUERY_RES(
			@JsonProperty("evtId") int evtId,
			@JsonProperty("result") Object result
	) {
		this.evtId = evtId;
		this.result = result;
	}

	public int getEvtId() {
		return evtId;
	}

	public Object getResult() {
		return result;
	}
}