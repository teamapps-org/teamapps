package org.teamapps.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("INIT_OK")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class INIT_OK extends AbstractServerMessage {

	@JsonProperty("minRequestedCommands")
	protected final int minRequestedCommands;
	@JsonProperty("maxRequestedCommands")
	protected final int maxRequestedCommands;
	@JsonProperty("sentEventsBufferSize")
	protected final int sentEventsBufferSize;
	@JsonProperty("keepaliveInterval")
	protected final long keepaliveInterval;

	@JsonCreator
	public INIT_OK(@JsonProperty("minRequestedCommands") int minRequestedCommands,
				   @JsonProperty("maxRequestedCommands") int maxRequestedCommands,
				   @JsonProperty("sentEventsBufferSize") int sentEventsBufferSize,
				   @JsonProperty("keepaliveInterval") long keepaliveInterval) {
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