package org.teamapps.ux.component.webrtc;

public class PublishedStreamsStatus {

	private final boolean audio;
	private final boolean video;

	public PublishedStreamsStatus(boolean audio, boolean video) {
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
