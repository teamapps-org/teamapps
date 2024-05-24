package org.teamapps.projector.components.core.splitpane;

import org.teamapps.projector.dto.DtoChildCollapsingPolicy;

public enum ChildCollapsingPolicy {

	NEVER,
	IF_NULL,
	IF_EMPTY;

	public DtoChildCollapsingPolicy toDto() {
		return DtoChildCollapsingPolicy.valueOf(name());
	}

}
