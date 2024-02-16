package org.teamapps.ux.component.splitpane;

import org.teamapps.dto.DtoChildCollapsingPolicy;

public enum ChildCollapsingPolicy {

	NEVER,
	IF_NULL,
	IF_EMPTY;

	public DtoChildCollapsingPolicy toDto() {
		return DtoChildCollapsingPolicy.valueOf(name());
	}

}
