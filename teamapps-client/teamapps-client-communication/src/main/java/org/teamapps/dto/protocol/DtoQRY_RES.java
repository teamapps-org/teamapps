package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("QRY_RES")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoQRY_RES extends DtoAbstractServerMessage {

	protected final int queryId;
	protected final Object result;

	@JsonCreator
	public DtoQRY_RES(@JsonProperty("queryId") int queryId, @JsonProperty("result") Object result) {
		super();
		this.queryId = queryId;
		this.result = result;
	}

	public int getQueryId() {
		return queryId;
	}

	public Object getResult() {
		return result;
	}

}