package org.teamapps.ux.component.webrtc;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiMediaSoupPlaybackParamaters;
import org.teamapps.dto.UiMediaSoupPublishingParameters;
import org.teamapps.dto.UiMediaSoupV2WebRtcClient;
import org.teamapps.dto.UiObject;
import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;
import org.teamapps.util.UiUtil;
import org.teamapps.ux.component.AbstractComponent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

public class MediaSoupV2WebRtcClient extends AbstractComponent {

	public final Event<MulticastPlaybackProfile> onPlaybackProfileChanged = new Event<>();
	public final Event<Boolean> onVoiceActivityChanged = new Event<>();
	public final Event<Void> onClicked = new Event<>();

	private String serverAddress;
	private int serverPort;

	private boolean activityLineVisible;
	private Color activityInactiveColor;
	private Color activityActiveColor;
	private boolean active;

	private Icon icon;
	private String caption;
	private String noVideoImageUrl;

	private Float displayAreaAspectRatio = 4/3f; // width / height. Makes the display always use this aspect ratio. If null, use 100% of available space

	private UiObject lastPublishOrPlaybackParams;

	public MediaSoupV2WebRtcClient() {
	}

	public MediaSoupV2WebRtcClient(String serverUrl) {
		URL url;
		try {
			url = new URL(serverUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		this.serverAddress = url.getHost();
		this.serverPort = url.getPort();
	}

	public MediaSoupV2WebRtcClient(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}

	@Override
	public UiMediaSoupV2WebRtcClient createUiComponent() {
		UiMediaSoupV2WebRtcClient ui = new UiMediaSoupV2WebRtcClient();
		mapAbstractUiComponentProperties(ui);
		ui.setInitialPlaybackOrPublishParams(lastPublishOrPlaybackParams);
		ui.setActivityLineVisible(activityLineVisible);
		ui.setActivityInactiveColor(UiUtil.createUiColor(activityInactiveColor));
		ui.setActivityActiveColor(UiUtil.createUiColor(activityActiveColor));
		ui.setIcon(getSessionContext().resolveIcon(icon));
		ui.setCaption(caption);
		ui.setNoVideoImageUrl(noVideoImageUrl);
		ui.setDisplayAreaAspectRatio(displayAreaAspectRatio);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_MEDIA_SOUP_V2_WEB_RTC_CLIENT_PLAYBACK_PROFILE_CHANGED: {
				UiMediaSoupV2WebRtcClient.PlaybackProfileChangedEvent e = (UiMediaSoupV2WebRtcClient.PlaybackProfileChangedEvent) event;
				if (e.getProfile() == null) {
					return;
				}
				MulticastPlaybackProfile profile = Arrays.stream(MulticastPlaybackProfile.values())
						.filter(p -> Objects.equals(p.name(), e.getProfile().name()))
						.findFirst().orElse(null);
				if (profile == null) {
					return;
				}
				this.onPlaybackProfileChanged.fire(MulticastPlaybackProfile.valueOf(e.getProfile().name()));
				break;
			}
			case UI_MEDIA_SOUP_V2_WEB_RTC_CLIENT_VOICE_ACTIVITY_CHANGED: {
				UiMediaSoupV2WebRtcClient.VoiceActivityChangedEvent e = (UiMediaSoupV2WebRtcClient.VoiceActivityChangedEvent) event;
				this.onVoiceActivityChanged.fire(e.getActive());
				break;
			}
			case UI_MEDIA_SOUP_V2_WEB_RTC_CLIENT_CLICKED: {
				UiMediaSoupV2WebRtcClient.ClickedEvent e = (UiMediaSoupV2WebRtcClient.ClickedEvent) event;
				this.onClicked.fire();
				break;
			}
		}
	}

	public void publish(String uid, String token, AudioTrackConstraints audioConstraints, VideoTrackConstraints videoConstraints, ScreenSharingConstraints screenSharingConstraints, long maxBitrate) {
		UiMediaSoupPublishingParameters params = new UiMediaSoupPublishingParameters();
		params.setServerAdress(serverAddress);
		params.setServerPort(serverPort);
		params.setUid(uid);
		params.setToken(token);
		params.setAudioConstraints(audioConstraints != null ? audioConstraints.createUiAudioTrackConstraints() : null);
		params.setVideoConstraints(videoConstraints != null ? videoConstraints.createUiVideoTrackConstraints() : null);
		params.setScreenSharingConstraints(screenSharingConstraints != null ? screenSharingConstraints.createUiScreenSharingConstraints() : null);
		params.setMaxBitrate(maxBitrate);

		if (this.isRendered()) {
			queueCommandIfRendered(() -> new UiMediaSoupV2WebRtcClient.PublishCommand(getId(), params));
		} else {
			lastPublishOrPlaybackParams = params;
		}
	}

	public void play(String uid, boolean audio, boolean video, long minBitrate, long maxBitrate) {
		UiMediaSoupPlaybackParamaters params = new UiMediaSoupPlaybackParamaters();
		params.setServerAdress(serverAddress);
		params.setServerPort(serverPort);
		params.setUid(uid);
		params.setAudio(audio);
		params.setVideo(video);
		params.setMinBitrate(minBitrate);
		params.setMaxBitrate(maxBitrate);

		if (this.isRendered()) {
			queueCommandIfRendered(() -> new UiMediaSoupV2WebRtcClient.PlaybackCommand(getId(), params));
		} else {
			lastPublishOrPlaybackParams = params;
		}
	}

	public void stop() {
		if (this.isRendered()) {
			queueCommandIfRendered(() -> new UiMediaSoupV2WebRtcClient.StopCommand(getId()));
		} else {
			lastPublishOrPlaybackParams = null;
		}
	}

	private void update() {
		queueCommandIfRendered(() -> new UiMediaSoupV2WebRtcClient.UpdateCommand(getId(), createUiComponent()));
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public boolean isActivityLineVisible() {
		return activityLineVisible;
	}

	public void setActivityLineVisible(boolean activityLineVisible) {
		this.activityLineVisible = activityLineVisible;
		update();
	}

	public Color getActivityInactiveColor() {
		return activityInactiveColor;
	}

	public void setActivityInactiveColor(Color activityInactiveColor) {
		this.activityInactiveColor = activityInactiveColor;
		update();
	}

	public Color getActivityActiveColor() {
		return activityActiveColor;
	}

	public void setActivityActiveColor(Color activityActiveColor) {
		this.activityActiveColor = activityActiveColor;
		update();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		queueCommandIfRendered(() -> new UiMediaSoupV2WebRtcClient.SetActiveCommand(getId(), active));
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
		update();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		update();
	}

	public String getNoVideoImageUrl() {
		return noVideoImageUrl;
	}

	public void setNoVideoImageUrl(String noVideoImageUrl) {
		this.noVideoImageUrl = noVideoImageUrl;
		update();
	}

	public UiObject getLastPublishOrPlaybackParams() {
		return lastPublishOrPlaybackParams;
	}

	public void setLastPublishOrPlaybackParams(UiObject lastPublishOrPlaybackParams) {
		this.lastPublishOrPlaybackParams = lastPublishOrPlaybackParams;
		update();
	}

	public Float getDisplayAreaAspectRatio() {
		return displayAreaAspectRatio;
	}

	public void setDisplayAreaAspectRatio(Float displayAreaAspectRatio) {
		this.displayAreaAspectRatio = displayAreaAspectRatio;
		update();
	}
}
