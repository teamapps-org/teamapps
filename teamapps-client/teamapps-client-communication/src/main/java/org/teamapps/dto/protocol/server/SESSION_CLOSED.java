package org.teamapps.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;


@JsonTypeName("SESSION_CLOSED")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SESSION_CLOSED extends AbstractServerMessage {

	@JsonProperty("reason")
	protected final SessionClosingReason reason;
	@JsonProperty("message")
	protected final String message;

	@JsonCreator
	public SESSION_CLOSED(@JsonProperty("reason") SessionClosingReason reason,
						  @JsonProperty("message") String message) {
		super();
		this.reason = reason;
		this.message = message;
	}

	public SessionClosingReason getReason() {
		return reason;
	}

	public String getMessage() {
		return message;
	}

}