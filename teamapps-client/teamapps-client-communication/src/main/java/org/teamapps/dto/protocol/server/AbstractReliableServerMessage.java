package org.teamapps.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractReliableServerMessage extends AbstractServerMessage {

	@JsonProperty("sn") int sequenceNumber;

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
}