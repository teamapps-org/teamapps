package org.teamapps.ux.component.webrtc;

public class TrackPublishingFailedEventData {

	private final boolean audio;
	private final boolean video;
	private final String errorMessage;

	public TrackPublishingFailedEventData(boolean audio, boolean video, String errorMessage) {
		this.audio = audio;
		this.video = video;
		this.errorMessage = errorMessage;
	}

	public boolean isAudio() {
		return audio;
	}

	public boolean isVideo() {
		return video;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public String toString() {
		return "TrackPublishingFailedEventData{" +
				"audio=" + audio +
				", video=" + video +
				", errorMessage='" + errorMessage + '\'' +
				'}';
	}
}
