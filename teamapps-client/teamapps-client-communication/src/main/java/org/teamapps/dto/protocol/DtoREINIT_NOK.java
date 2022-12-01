package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("REINIT_NOK")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoREINIT_NOK extends DtoAbstractServerMessage {

	protected final DtoSessionClosingReason reason;

	@JsonCreator
	public DtoREINIT_NOK(@JsonProperty("reason") DtoSessionClosingReason reason) {
		super();
		this.reason = reason;
	}

	public DtoSessionClosingReason getReason() {
		return reason;
	}

}