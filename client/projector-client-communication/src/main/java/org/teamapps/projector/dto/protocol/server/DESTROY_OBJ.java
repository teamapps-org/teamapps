package org.teamapps.projector.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("DESTROY_OBJ")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DESTROY_OBJ extends AbstractReliableServerMessage {

	@JsonProperty("oid")
	protected final String oid;

	@JsonCreator
	public DESTROY_OBJ(
			@JsonProperty("oid") String oid
	) {
		this.oid = oid;
	}

	public String getOid() {
		return oid;
	}

}