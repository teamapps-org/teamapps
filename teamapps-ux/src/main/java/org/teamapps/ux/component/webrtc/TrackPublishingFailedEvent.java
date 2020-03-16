package org.teamapps.ux.component.webrtc;

public class TrackPublishingFailedEvent {

	private final PublishedMediaTrackType trackType;
	private final String errorMessage;

	public TrackPublishingFailedEvent(PublishedMediaTrackType trackType, String errorMessage) {
		this.trackType = trackType;
		this.errorMessage = errorMessage;
	}

	public PublishedMediaTrackType getTrackType() {
		return trackType;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
