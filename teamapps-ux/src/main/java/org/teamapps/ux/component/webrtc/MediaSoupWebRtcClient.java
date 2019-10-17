package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiMediaSoupPlaybackParamaters;
import org.teamapps.dto.UiMediaSoupPublishingParameters;
import org.teamapps.dto.UiMediaSoupWebRtcClient;
import org.teamapps.ux.component.AbstractComponent;

public class MediaSoupWebRtcClient extends AbstractComponent {

	private String serverUrl;

	@Override
	public UiComponent createUiComponent() {
		UiMediaSoupWebRtcClient ui = new UiMediaSoupWebRtcClient();
		mapAbstractUiComponentProperties(ui);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {

	}

	public void publish(String uid, String token, AudioTrackConstraints audioConstraints, VideoTrackConstraints videoConstraints, long minBitrate, long maxBitrate) {
		UiMediaSoupPublishingParameters params = new UiMediaSoupPublishingParameters();
		params.setServerUrl(serverUrl);
		params.setUid(uid);
		params.setToken(token);
		params.setAudioConstraints(audioConstraints != null ? audioConstraints.createUiAudioTrackConstraints() : null);
		params.setVideoConstraints(videoConstraints != null ? videoConstraints.createUiVideoTrackConstraints() : null);
		params.setMinBitrate(minBitrate);
		params.setMaxBitrate(maxBitrate);

		queueCommandIfRendered(() -> new UiMediaSoupWebRtcClient.PublishCommand(getId(), params));
	}

	public void play(String uid, boolean audio, boolean video, long minBitrate, long maxBitrate) {
		UiMediaSoupPlaybackParamaters params = new UiMediaSoupPlaybackParamaters();
		params.setServerUrl(serverUrl);
		params.setUid(uid);
		params.setAudio(audio);
		params.setVideo(video);
		params.setMinBitrate(minBitrate);
		params.setMaxBitrate(maxBitrate);

		queueCommandIfRendered(() -> new UiMediaSoupWebRtcClient.PlaybackCommand(getId(), params));
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

}
