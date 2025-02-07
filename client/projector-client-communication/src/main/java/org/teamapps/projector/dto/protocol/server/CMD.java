package org.teamapps.projector.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("CMD")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CMD extends AbstractReliableServerMessage {

	@JsonProperty("lid")
	private final String libraryUuid;

	@JsonProperty("oid")
	private final String clientObjectId;

	@JsonProperty("name")
	private final String name;

	@JsonProperty("params")
	private final Object[] params;

	@JsonProperty("r")
	private final Boolean awaitsResponse; // nullable! (for message size reasons)

	public CMD(@JsonProperty("lid") String libraryUuid,
			   @JsonProperty("oid") String clientObjectId,
			   @JsonProperty("name") String name,
			   @JsonProperty("params") Object[] params,
			   @JsonProperty("r") Boolean awaitsResponse) {
		this.libraryUuid = libraryUuid;
		this.clientObjectId = clientObjectId;
		this.name = name;
		this.params = params;
		this.awaitsResponse = awaitsResponse;
	}

	public String getLibraryUuid() {
		return libraryUuid;
	}

	public String getClientObjectId() {
		return clientObjectId;
	}

	public String getName() {
		return name;
	}

	public Object[] getParams() {
		return params;
	}

	public Boolean getAwaitsResponse() {
		return awaitsResponse;
	}

	@Override
	public String toString() {
		return "CMD{" +
			   "libraryUuid='" + libraryUuid + '\'' +
			   ", clientObjectId='" + clientObjectId + '\'' +
			   ", name='" + name + '\'' +
			   ", params='" + params + '\'' +
			   ", awaitsResponse=" + awaitsResponse +
			   '}';
	}
}