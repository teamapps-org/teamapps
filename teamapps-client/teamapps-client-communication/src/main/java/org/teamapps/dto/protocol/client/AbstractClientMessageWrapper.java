package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.dto.JsonWrapper;


public class AbstractClientMessageWrapper extends JsonWrapper {

	public AbstractClientMessageWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

}