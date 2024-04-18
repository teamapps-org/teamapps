package org.teamapps.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("TOGGLE_EVT")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TOGGLE_EVT extends AbstractReliableServerMessage {

	@JsonProperty("lid")
	private final String lid;
	@JsonProperty("oid")
	private final String oid;
	@JsonProperty("evtName")
	private final String evtName;
	@JsonProperty("enabled")
	private final boolean enabled;

	@JsonCreator
	public TOGGLE_EVT(
			@JsonProperty("lid") String lid,
			@JsonProperty("oid") String oid,
			@JsonProperty("evtName") String evtName,
			@JsonProperty("enabled") boolean enabled
	) {
		this.lid = lid;
		this.oid = oid;
		this.evtName = evtName;
		this.enabled = enabled;
	}

	public String getLibraryId() {
		return lid;
	}

	public String getClientObjectId() {
		return oid;
	}

	public String getEventName() {
		return evtName;
	}

	public boolean isEnabled() {
		return enabled;
	}
}