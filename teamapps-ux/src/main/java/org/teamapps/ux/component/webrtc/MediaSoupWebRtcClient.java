package org.teamapps.ux.component.webrtc;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiMediaSoupPlaybackParamaters;
import org.teamapps.dto.UiMediaSoupPublishingParameters;
import org.teamapps.dto.UiMediaSoupWebRtcClient;
import org.teamapps.dto.UiObject;
import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;
import org.teamapps.util.UiUtil;
import org.teamapps.ux.component.AbstractComponent;

public class MediaSoupWebRtcClient extends AbstractComponent {

	public final Event<MulticastPlaybackProfile> onPlaybackProfileChanged = new Event<>();
	public final Event<Boolean> onActivityChanged = new Event<>();

	private String serverUrl;

	private boolean activityLineVisible;
	private Color activityInactiveColor;
	private Color activityActiveColor;
	private boolean active;

	private Icon icon;
	private String caption;
	private String noVideoImageUrl;

	private Float displayAreaAspectRatio = 4/3f; // width / height. Makes the display always use this aspect ratio. If null, use 100% of available space

	private UiObject lastPublishOrPlaybackParams;

	public MediaSoupWebRtcClient() {
	}

	public MediaSoupWebRtcClient(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	@Override
	public UiMediaSoupWebRtcClient createUiComponent() {
		UiMediaSoupWebRtcClient ui = new UiMediaSoupWebRtcClient();
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
			case UI_MEDIA_SOUP_WEB_RTC_CLIENT_PLAYBACK_PROFILE_CHANGED: {
				UiMediaSoupWebRtcClient.PlaybackProfileChangedEvent e = (UiMediaSoupWebRtcClient.PlaybackProfileChangedEvent) event;
				this.onPlaybackProfileChanged.fire(MulticastPlaybackProfile.valueOf(e.getProfile().name()));
				break;
			}
			case UI_MEDIA_SOUP_WEB_RTC_CLIENT_ACTIVITY_CHANGED: {
				UiMediaSoupWebRtcClient.ActivityChangedEvent e = (UiMediaSoupWebRtcClient.ActivityChangedEvent) event;
				this.onActivityChanged.fire(e.getActive());
				break;
			}
		}
	}

	public void publish(String uid, String token, AudioTrackConstraints audioConstraints, VideoTrackConstraints videoConstraints, ScreenSharingConstraints screenSharingConstraints, long maxBitrate) {
		UiMediaSoupPublishingParameters params = new UiMediaSoupPublishingParameters();
		params.setServerUrl(serverUrl);
		params.setUid(uid);
		params.setToken(token);
		params.setAudioConstraints(audioConstraints != null ? audioConstraints.createUiAudioTrackConstraints() : null);
		params.setVideoConstraints(videoConstraints != null ? videoConstraints.createUiVideoTrackConstraints() : null);
		params.setScreenSharingConstraints(screenSharingConstraints != null ? screenSharingConstraints.createUiScreenSharingConstraints() : null);
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

	private void update() {
		queueCommandIfRendered(() -> new UiMediaSoupWebRtcClient.UpdateCommand(getId(), createUiComponent()));
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
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
		queueCommandIfRendered(() -> new UiMediaSoupWebRtcClient.SetActiveCommand(getId(), active));
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
