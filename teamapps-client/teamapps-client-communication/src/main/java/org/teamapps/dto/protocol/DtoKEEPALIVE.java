package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("KEEPALIVE")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoKEEPALIVE extends DtoAbstractClientMessage {

	public static final String TYPE_ID = "KEEPALIVE";

	@JsonCreator
	public DtoKEEPALIVE(@JsonProperty("sessionId") String sessionId) {
		super(sessionId);
	}

}