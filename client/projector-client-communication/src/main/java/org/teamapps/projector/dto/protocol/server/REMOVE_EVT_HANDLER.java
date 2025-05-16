package org.teamapps.projector.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("REMOVE_EVT_HANDLER")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class REMOVE_EVT_HANDLER extends AbstractReliableServerMessage {

	@JsonProperty("lid")
	private final String lid;
	@JsonProperty("oid")
	private final String oid;
	@JsonProperty("evtName")
	private final String evtName;
	@JsonProperty("registrationId")
	private final String registrationId;

	@JsonCreator
	public REMOVE_EVT_HANDLER(
			@JsonProperty("lid") String lid,
			@JsonProperty("oid") String oid,
			@JsonProperty("evtName") String evtName,
			@JsonProperty("registrationId") String registrationId
			) {
		this.lid = lid;
		this.oid = oid;
		this.evtName = evtName;
		this.registrationId = registrationId;
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

	public String getRegistrationId() {
		return registrationId;
	}

}