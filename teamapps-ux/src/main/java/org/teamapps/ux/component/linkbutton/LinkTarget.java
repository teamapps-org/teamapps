package org.teamapps.ux.component.linkbutton;

import org.teamapps.dto.UiLinkTarget;

public enum LinkTarget {
	SELF, BLANK, PARENT, TOP;

	public UiLinkTarget toUiLinkTarget() {
		return UiLinkTarget.valueOf(name());
	}
}