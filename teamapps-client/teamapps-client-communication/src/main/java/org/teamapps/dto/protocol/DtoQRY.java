package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.teamapps.dto.DtoQuery;



@JsonTypeName("QRY")
public class DtoQRY extends DtoAbstractClientPayloadMessage {

	public static final String TYPE_ID = "QRY";

	protected final DtoQuery uiQuery;

	@JsonCreator
	public DtoQRY(@JsonProperty("sessionId") String sessionId, @JsonProperty("id") int id, @JsonProperty("uiQuery") DtoQuery uiQuery) {
		super(sessionId, id);
		this.uiQuery = uiQuery;
	}

	public DtoQuery getUiQuery() {
		return uiQuery;
	}

}