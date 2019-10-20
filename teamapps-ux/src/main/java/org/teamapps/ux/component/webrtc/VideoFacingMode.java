package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiVideoFacingMode;

public enum VideoFacingMode {

	USER,
	ENVIRONMENT,
	LEFT,
	RIGHT;

	public UiVideoFacingMode toUiVideoFacingMode() {
		return UiVideoFacingMode.valueOf(name());
	}

}
