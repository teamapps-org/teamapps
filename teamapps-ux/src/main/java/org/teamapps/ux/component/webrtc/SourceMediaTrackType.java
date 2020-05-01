package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiSourceMediaTrackType;

public enum SourceMediaTrackType {

	CAM, MIC, SCREEN;

	public UiSourceMediaTrackType toUiSourceMediaTrackType() {
		return UiSourceMediaTrackType.valueOf(this.name());
	}

}
