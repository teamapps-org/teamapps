package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiMediaSoupPlaybackParamaters;
import org.teamapps.dto.UiMediaSoupPublishingParameters;
import org.teamapps.dto.UiMediaSoupWebRtcClient;
import org.teamapps.dto.UiObject;
import org.teamapps.ux.component.AbstractComponent;

public class MediaSoupWebRtcClient extends AbstractComponent {

	private String serverUrl;

	private UiObject lastPublishOrPlaybackParams;

	public MediaSoupWebRtcClient() {
	}

	public MediaSoupWebRtcClient(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	@Override
	public UiComponent createUiComponent() {
		UiMediaSoupWebRtcClient ui = new UiMediaSoupWebRtcClient();
		mapAbstractUiComponentProperties(ui);
		ui.setInitialPlaybackOrPublishParams(lastPublishOrPlaybackParams);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {

	}

	public void publish(String uid, String token, AudioTrackConstraints audioConstraints, VideoTrackConstraints videoConstraints, long maxBitrate) {
		UiMediaSoupPublishingParameters params = new UiMediaSoupPublishingParameters();
		params.setServerUrl(serverUrl);
		params.setUid(uid);
		params.setToken(token);
		params.setAudioConstraints(audioConstraints != null ? audioConstraints.createUiAudioTrackConstraints() : null);
		params.setVideoConstraints(videoConstraints != null ? videoConstraints.createUiVideoTrackConstraints() : null);
		params.setMaxBitrate(maxBitrate);

		if (this.isRendered()) {
			queueCommandIfRendered(() -> new UiMediaSoupWebRtcClient.PublishCommand(getId(), params));
		} else {
			lastPublishOrPlaybackParams = params;
		}
	}

	public void play(String uid, boolean audio, boolean video, long minBitrate, long maxBitrate) {
		UiMediaSoupPlaybackParamaters params = new UiMediaSoupPlaybackParamaters();
		params.setServerUrl(serverUrl);
		params.setUid(uid);
		params.setAudio(audio);
		params.setVideo(video);
		params.setMinBitrate(minBitrate);
		params.setMaxBitrate(maxBitrate);

		if (this.isRendered()) {
			queueCommandIfRendered(() -> new UiMediaSoupWebRtcClient.PlaybackCommand(getId(), params));
		} else {
			lastPublishOrPlaybackParams = params;
		}
	}

	public void stop() {
		if (this.isRendered()) {
			queueCommandIfRendered(() -> new UiMediaSoupWebRtcClient.StopCommand(getId()));
		} else {
			lastPublishOrPlaybackParams = null;
		}
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

}
