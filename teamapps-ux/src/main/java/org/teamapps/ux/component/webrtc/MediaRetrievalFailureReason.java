package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiMediaRetrievalFailureReason;

public enum MediaRetrievalFailureReason {
	MIC_MEDIA_RETRIEVAL_FAILED,
	CAM_MEDIA_RETRIEVAL_FAILED,
	DISPLAY_MEDIA_RETRIEVAL_FAILED,
	VIDEO_MIXING_FAILED;

	public UiMediaRetrievalFailureReason toUiMediaRetrievalFailureReason() {
		return UiMediaRetrievalFailureReason.valueOf(this.name());
	}
}
