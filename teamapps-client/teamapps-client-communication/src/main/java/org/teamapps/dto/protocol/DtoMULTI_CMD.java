package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName("MULTI_CMD")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoMULTI_CMD extends DtoAbstractServerMessage {

	protected final List<CMD> cmds;

	@JsonCreator
	public DtoMULTI_CMD(@JsonProperty("cmds") List<CMD> cmds) {
		super();
		this.cmds = cmds;
	}

	public List<CMD> getCmds() {
		return cmds;
	}

}