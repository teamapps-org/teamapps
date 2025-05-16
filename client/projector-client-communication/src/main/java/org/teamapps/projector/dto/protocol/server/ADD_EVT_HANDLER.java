package org.teamapps.projector.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName("ADD_EVT_HANDLER")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ADD_EVT_HANDLER extends AbstractReliableServerMessage {

	@JsonProperty("lid")
	private final String lid;
	@JsonProperty("oid")
	private final String oid;
	@JsonProperty("evtName")
	private final String evtName;
	@JsonProperty("registrationId")
	private final String registrationId;
	@JsonProperty("invokableId")
	private final String invokableId;
	@JsonProperty("functionName")
	private final String functionName;
	@JsonProperty("eventObjectAsFirstParameter")
	private final boolean evtObjAsFirstParam;
	@JsonProperty("params")
	private final List<Object> params;

	@JsonCreator
	public ADD_EVT_HANDLER(
			@JsonProperty("lid") String lid,
			@JsonProperty("oid") String oid,
			@JsonProperty("evtName") String evtName,
			@JsonProperty("registrationId") String registrationId,
			@JsonProperty("invokableId") String invokableId,
			@JsonProperty("functionName") String functionName,
			@JsonProperty("evtObjAsFirstParam") boolean evtObjAsFirstParam,
			@JsonProperty("params") List<Object> params
	) {
		this.lid = lid;
		this.oid = oid;
		this.evtName = evtName;
		this.registrationId = registrationId;
		this.invokableId = invokableId;
		this.functionName = functionName;
		this.evtObjAsFirstParam = evtObjAsFirstParam;
		this.params = params;
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

	public String getInvokableId() {
		return invokableId;
	}

	public String getFunctionName() {
		return functionName;
	}

	public boolean isEvtObjAsFirstParam() {
		return evtObjAsFirstParam;
	}

	public List<Object> getParams() {
		return params;
	}
}