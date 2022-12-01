package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("SESSION_CLOSED")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoSESSION_CLOSED extends DtoAbstractServerMessage {

	protected final DtoSessionClosingReason reason;
	protected final String message;

	@JsonCreator
	public DtoSESSION_CLOSED(@JsonProperty("reason") DtoSessionClosingReason reason, @JsonProperty("message") String message) {
		super();
		this.reason = reason;
		this.message = message;
	}

	public DtoSessionClosingReason getReason() {
		return reason;
	}

	public String getMessage() {
		return message;
	}

}