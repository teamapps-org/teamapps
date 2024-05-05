package org.teamapps.ux.component.panel;

import org.teamapps.projector.dto.DtoPanelHeaderFieldIconVisibilityPolicy;

public enum HeaderFieldIconVisibilityPolicy {
	DISPLAYED_WHEN_MINIMIZED,
	ALWAYS_DISPLAYED         ;

	DtoPanelHeaderFieldIconVisibilityPolicy toDto() {
		return DtoPanelHeaderFieldIconVisibilityPolicy.valueOf(this.name());
	}
}
