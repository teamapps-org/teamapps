package org.teamapps.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("PING")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PING extends AbstractServerMessage {

	public PING() {
		super();
	}

}