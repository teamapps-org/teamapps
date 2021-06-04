package org.teamapps.ux.component.media;

import org.teamapps.dto.UiTrackLabelFormat;

public enum TrackLabelFormat {

	LABEL,
	LANGUAGE,
	LANGUAGE_ROLE,
	ROLE;

	public UiTrackLabelFormat toUiTrackLabelFormat() {
		return UiTrackLabelFormat.valueOf(this.name());
	}

}
