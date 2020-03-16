package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiPublishedMediaTrackType;

public enum PublishedMediaTrackType {
	AUDIO,
	VIDEO;

	public UiPublishedMediaTrackType toUiPublishedMediaTrackType() {
		return UiPublishedMediaTrackType.valueOf(this.name());
	}
}
