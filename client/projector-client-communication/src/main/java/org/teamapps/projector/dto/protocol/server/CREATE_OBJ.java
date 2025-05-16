package org.teamapps.projector.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName("CREATE_OBJ")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CREATE_OBJ extends AbstractReliableServerMessage {

	public record EventHandlerRegistration(
			@JsonProperty("evtName") String evtName,
			@JsonProperty("registrationId") String registrationId,
			@JsonProperty("invokableId") String invokableId,
			@JsonProperty("functionName") String functionName,
			@JsonProperty("evtObjAsFirstParam") boolean evtObjAsFirstParam,
			@JsonProperty("params") List<Object> params
	) {
	}

	@JsonProperty("lid")
	protected final String lid;
	@JsonProperty("typeName")
	protected final String typeName;
	@JsonProperty("oid")
	protected final String oid;
	@JsonProperty("config")
	protected final Object config;
	@JsonProperty("evtNames")
	protected final List<String> evtNames;
	@JsonProperty("evtHandlers")
	protected final List<EventHandlerRegistration> evtHandlers;

	@JsonCreator
	public CREATE_OBJ(
			@JsonProperty("lid") String libraryId,
			@JsonProperty("typeName") String typeName,
			@JsonProperty("oid") String oid,
			@JsonProperty("config") Object config,
			@JsonProperty("evtNames") List<String> evtNames,
			@JsonProperty("evtHandlers") List<EventHandlerRegistration> evtHandlers
	) {
		this.lid = libraryId;
		this.typeName = typeName;
		this.oid = oid;
		this.config = config;
		this.evtNames = evtNames;
		this.evtHandlers = evtHandlers;
	}

	public String getLid() {
		return lid;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getOid() {
		return oid;
	}

	public Object getConfig() {
		return config;
	}

	public List<String> getEvtNames() {
		return evtNames;
	}

	@Override
	public String toString() {
		return "CREATE_OBJ{" +
			   "lid='" + lid + '\'' +
			   ", typeName='" + typeName + '\'' +
			   ", oid='" + oid + '\'' +
			   ", config=" + config +
			   ", evtNames=" + evtNames +
			   '}';
	}
}