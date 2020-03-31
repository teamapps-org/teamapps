package org.teamapps.ux.component.webrtc;

public class TrackPublishingSuccessfulEventData {

	private final boolean audio;
	private final boolean video;

	public TrackPublishingSuccessfulEventData(boolean audio, boolean video) {
		this.audio = audio;
		this.video = video;
	}

	public boolean isAudio() {
		return audio;
	}

	public boolean isVideo() {
		return video;
	}

}
