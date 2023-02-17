package org.teamapps.ux.component.media.shaka;

public class SkipClickedEvent {

	private final boolean forward;
	private final long playbackTimeMillis;

	public SkipClickedEvent(boolean forward, long playbackTimeMillis) {
		this.forward = forward;
		this.playbackTimeMillis = playbackTimeMillis;
	}

	public boolean isForward() {
		return forward;
	}

	public long getPlaybackTimeMillis() {
		return playbackTimeMillis;
	}
}
