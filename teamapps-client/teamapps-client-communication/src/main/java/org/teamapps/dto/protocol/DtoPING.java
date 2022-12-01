package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("PING")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoPING extends DtoAbstractServerMessage {

	public DtoPING() {
		super();
	}

}