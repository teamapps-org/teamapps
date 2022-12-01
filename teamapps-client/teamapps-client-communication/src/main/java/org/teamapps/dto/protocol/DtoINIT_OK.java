package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("INIT_OK")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoINIT_OK extends DtoAbstractServerMessage {

	protected final int minRequestedCommands;
	protected final int maxRequestedCommands;
	protected final int sentEventsBufferSize;
	protected final long keepaliveInterval;

	@JsonCreator
	public DtoINIT_OK(@JsonProperty("minRequestedCommands") int minRequestedCommands, @JsonProperty("maxRequestedCommands") int maxRequestedCommands, @JsonProperty("sentEventsBufferSize") int sentEventsBufferSize, @JsonProperty("keepaliveInterval") long keepaliveInterval) {
		super();
		this.minRequestedCommands = minRequestedCommands;
		this.maxRequestedCommands = maxRequestedCommands;
		this.sentEventsBufferSize = sentEventsBufferSize;
		this.keepaliveInterval = keepaliveInterval;
	}

	public int getMinRequestedCommands() {
		return minRequestedCommands;
	}

	public int getMaxRequestedCommands() {
		return maxRequestedCommands;
	}

	public int getSentEventsBufferSize() {
		return sentEventsBufferSize;
	}

	public long getKeepaliveInterval() {
		return keepaliveInterval;
	}

}