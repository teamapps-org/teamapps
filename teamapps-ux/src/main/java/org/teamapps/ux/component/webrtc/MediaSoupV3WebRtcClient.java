/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.ux.component.webrtc;

import org.teamapps.common.format.Color;
import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.toolbutton.ToolButton;
import org.teamapps.ux.session.SessionContext;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MediaSoupV3WebRtcClient extends AbstractComponent {

	public final Event<MediaRetrievalFailureReason> onSourceMediaTrackRetrievalFailed = new Event<>();
	public final Event<SourceMediaTrackType> onSourceMediaTrackEnded = new Event<>();
	public final Event<TrackPublishingSuccessfulEventData> onTrackPublishingSuccessful = new Event<>();
	public final Event<TrackPublishingFailedEventData> onTrackPublishingFailed = new Event<>();

	public final Event<WebRtcStreamType> onPublishedStreamEnded = new Event<>();

	public final Event<Void> onSubscribingSuccessful = new Event<>();
	public final Event<String> onSubscribingFailed = new Event<>();

	public final Event<Boolean> onConnectionStateChanged = new Event<>();
	public final Event<Boolean> onVoiceActivityChanged = new Event<>();
	public final Event<Void> onClicked = new Event<>();

	private boolean activityLineVisible;
	private Color activityInactiveColor;
	private Color activityActiveColor;
	private boolean active;

	private List<ToolButton> toolButtons = Collections.emptyList();
	private List<Icon> icons = Collections.emptyList();
	private String caption;
	private String noVideoImageUrl;

	private Float displayAreaAspectRatio; // width / height. Makes the display always use this aspect ratio. If null, use 100% of available space

	private double playbackVolume = 1;

	private UiMediaSoupPublishingParameters publishingParameters;
	private UiMediaSoupPlaybackParameters playbackParameters;

	private Supplier<Component> contextMenuProvider = null;
	private int lastSeenContextMenuRequestId;

	private boolean bitrateDisplayEnabled;

	private int forceRefreshCount = 0;

	public MediaSoupV3WebRtcClient() {
	}

	@Override
	public UiMediaSoupV3WebRtcClient createUiComponent() {
		UiMediaSoupV3WebRtcClient ui = new UiMediaSoupV3WebRtcClient();
		mapAbstractUiComponentProperties(ui);
		ui.setPublishingParameters(publishingParameters);
		ui.setPlaybackParameters(playbackParameters);
		ui.setActivityLineVisible(activityLineVisible);
		ui.setActivityInactiveColor(activityInactiveColor != null ? activityInactiveColor.toHtmlColorString() : null);
		ui.setActivityActiveColor(activityActiveColor != null ? activityActiveColor.toHtmlColorString() : null);
		ui.setToolButtons(toolButtons.stream().map(AbstractComponent::createUiReference).collect(Collectors.toList()));
		ui.setIcons(icons.stream().map(icon -> getSessionContext().resolveIcon(icon)).collect(Collectors.toList()));
		ui.setBitrateDisplayEnabled(bitrateDisplayEnabled);
		ui.setCaption(caption);
		ui.setNoVideoImageUrl(noVideoImageUrl);
		ui.setDisplayAreaAspectRatio(displayAreaAspectRatio);
		ui.setPlaybackVolume(playbackVolume);
		ui.setContextMenuEnabled(contextMenuProvider != null);
		ui.setForceRefreshCount(forceRefreshCount);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_VOICE_ACTIVITY_CHANGED: {
				UiMediaSoupV3WebRtcClient.VoiceActivityChangedEvent e = (UiMediaSoupV3WebRtcClient.VoiceActivityChangedEvent) event;
				this.onVoiceActivityChanged.fire(e.getActive());
				break;
			}
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_CLICKED: {
				UiMediaSoupV3WebRtcClient.ClickedEvent e = (UiMediaSoupV3WebRtcClient.ClickedEvent) event;
				this.onClicked.fire();
				break;
			}
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_SOURCE_MEDIA_TRACK_RETRIEVAL_FAILED: {
				UiMediaSoupV3WebRtcClient.SourceMediaTrackRetrievalFailedEvent e = (UiMediaSoupV3WebRtcClient.SourceMediaTrackRetrievalFailedEvent) event;
				this.onSourceMediaTrackRetrievalFailed.fire(MediaRetrievalFailureReason.valueOf(e.getReason().name()));
				break;
			}
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_SOURCE_MEDIA_TRACK_ENDED: {
				UiMediaSoupV3WebRtcClient.SourceMediaTrackEndedEvent e = (UiMediaSoupV3WebRtcClient.SourceMediaTrackEndedEvent) event;
				this.onSourceMediaTrackEnded.fire(SourceMediaTrackType.valueOf(e.getTrackType().name()));
				break;
			}
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_TRACK_PUBLISHING_SUCCESSFUL: {
				UiMediaSoupV3WebRtcClient.TrackPublishingSuccessfulEvent e = (UiMediaSoupV3WebRtcClient.TrackPublishingSuccessfulEvent) event;
				this.onTrackPublishingSuccessful.fire(new TrackPublishingSuccessfulEventData(e.getAudio(), e.getVideo()));
				break;
			}
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_TRACK_PUBLISHING_FAILED: {
				UiMediaSoupV3WebRtcClient.TrackPublishingFailedEvent e = (UiMediaSoupV3WebRtcClient.TrackPublishingFailedEvent) event;
				this.onTrackPublishingFailed.fire(new TrackPublishingFailedEventData(e.getAudio(), e.getVideo(), e.getErrorMessage()));
				break;
			}
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_SUBSCRIBING_SUCCESSFUL: {
				UiMediaSoupV3WebRtcClient.SubscribingSuccessfulEvent e = (UiMediaSoupV3WebRtcClient.SubscribingSuccessfulEvent) event;
				this.onSubscribingSuccessful.fire();
				break;
			}
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_SUBSCRIBING_FAILED: {
				UiMediaSoupV3WebRtcClient.SubscribingFailedEvent e = (UiMediaSoupV3WebRtcClient.SubscribingFailedEvent) event;
				this.onSubscribingFailed.fire(e.getErrorMessage());
				break;
			}
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_CONNECTION_STATE_CHANGED:
				this.onConnectionStateChanged.fire(((UiMediaSoupV3WebRtcClient.ConnectionStateChangedEvent) event).getConnected());
				break;
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_CONTEXT_MENU_REQUESTED: {
				UiMediaSoupV3WebRtcClient.ContextMenuRequestedEvent e = (UiMediaSoupV3WebRtcClient.ContextMenuRequestedEvent) event;
				lastSeenContextMenuRequestId = e.getRequestId();
				if (contextMenuProvider != null) {
					Component contextMenuContent = contextMenuProvider.get();
					if (contextMenuContent != null) {
						queueCommandIfRendered(() -> new UiInfiniteItemView.SetContextMenuContentCommand(getId(), e.getRequestId(), contextMenuContent.createUiReference()));
					} else {
						queueCommandIfRendered(() -> new UiInfiniteItemView.CloseContextMenuCommand(getId(), e.getRequestId()));
					}
				} else {
					closeContextMenu();
				}
				break;
			}
		}
	}

	public void publish(
			String streamUuid, String serverUrl, int worker, String token,
			AudioTrackConstraints audioConstraints,
			VideoTrackConstraints videoConstraints,
			ScreenSharingConstraints screenSharingConstraints,
			long maxBitrate, boolean simulcast,
			long keyFrameRequestDelayMillis) {
		UiMediaSoupPublishingParameters params = new UiMediaSoupPublishingParameters();
		params.setServer(new UiMediaServerUrlAndToken(serverUrl, worker, token));
		params.setStreamUuid(streamUuid);
		params.setAudioConstraints(audioConstraints != null ? audioConstraints.createUiAudioTrackConstraints() : null);
		params.setVideoConstraints(videoConstraints != null ? videoConstraints.createUiVideoTrackConstraints() : null);
		params.setScreenSharingConstraints(screenSharingConstraints != null ? screenSharingConstraints.createUiScreenSharingConstraints() : null);
		params.setMaxBitrate(maxBitrate);
		params.setSimulcast(simulcast);
		params.setKeyFrameRequestDelay(keyFrameRequestDelayMillis);
		this.publishingParameters = params;
		update();
	}

	public void play(
			String streamUuid,
			MediaSoupServerUrlAndToken server,
			boolean audio, boolean video,
			long minBitrate, long maxBitrate
	) {
		this.play(streamUuid, server, null, audio, video, minBitrate, maxBitrate);
	}

	public void play(
			String streamUuid,
			MediaSoupServerUrlAndToken server,
			MediaSoupServerUrlAndToken origin,
			boolean audio, boolean video,
			long minBitrate, long maxBitrate
	) {
		UiMediaSoupPlaybackParameters params = new UiMediaSoupPlaybackParameters();
		params.setStreamUuid(streamUuid);
		params.setServer(new UiMediaServerUrlAndToken(server.getServerUrl(), server.getWorker(), server.getToken()));
		params.setOrigin(origin != null ? new UiMediaServerUrlAndToken(origin.getServerUrl(), origin.getWorker(), origin.getToken()) : null);
		params.setAudio(audio);
		params.setVideo(video);
		params.setMinBitrate(minBitrate);
		params.setMaxBitrate(maxBitrate);
		this.playbackParameters = params;
		update();
	}

	public void stop() {
		publishingParameters = null;
		playbackParameters = null;
		update();
	}

	private void update() {
		queueCommandIfRendered(() -> new UiMediaSoupV3WebRtcClient.UpdateCommand(getId(), createUiComponent()));
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
		if (active != this.active) {
			this.active = active;
			queueCommandIfRendered(() -> new UiMediaSoupV3WebRtcClient.SetActiveCommand(getId(), active));
		}
	}

	public List<Icon> getIcons() {
		return icons;
	}

	public void setIcons(List<Icon> icons) {
		if (icons == null) {
			icons = Collections.emptyList();
		}
		if (!Objects.equals(icons, this.icons)) {
			this.icons = List.copyOf(icons);
			update();
		}
	}

	public List<ToolButton> getToolButtons() {
		return toolButtons;
	}

	public void setToolButtons(List<ToolButton> toolButtons) {
		if (toolButtons == null) {
			toolButtons = Collections.emptyList();
		}
		if (!Objects.equals(toolButtons, this.toolButtons)) {
			this.toolButtons = List.copyOf(toolButtons);
			update();
		}
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		if (!Objects.equals(caption, this.caption)) {
			this.caption = caption;
			update();
		}
	}

	public String getNoVideoImageUrl() {
		return noVideoImageUrl;
	}

	public void setNoVideoImageUrl(String noVideoImageUrl) {
		if (!Objects.equals(noVideoImageUrl, this.noVideoImageUrl)) {
			this.noVideoImageUrl = noVideoImageUrl;
			update();
		}
	}

	public Float getDisplayAreaAspectRatio() {
		return displayAreaAspectRatio;
	}

	public void setDisplayAreaAspectRatio(Float displayAreaAspectRatio) {
		if (!Objects.equals(displayAreaAspectRatio, this.displayAreaAspectRatio)) {
			this.displayAreaAspectRatio = displayAreaAspectRatio;
			update();
		}
	}

	public double getPlaybackVolume() {
		return playbackVolume;
	}

	public void setPlaybackVolume(double playbackVolume) {
		if (playbackVolume != this.playbackVolume) {
			this.playbackVolume = playbackVolume;
			update();
		}
	}

	public boolean isBitrateDisplayEnabled() {
		return bitrateDisplayEnabled;
	}

	public void setBitrateDisplayEnabled(boolean bitrateDisplayEnabled) {
		if (bitrateDisplayEnabled != this.bitrateDisplayEnabled) {
			this.bitrateDisplayEnabled = bitrateDisplayEnabled;
			update();
		}
	}

	public static CompletableFuture<List<UiMediaDeviceInfo>> enumerateDevices() {
		CompletableFuture<List<UiMediaDeviceInfo>> future = new CompletableFuture<>();
		SessionContext.current().queueCommand(new UiMediaSoupV3WebRtcClient.EnumerateDevicesCommand(), value -> {
			future.complete(value);
		});
		return future;
	}

	public Supplier<Component> getContextMenuProvider() {
		return contextMenuProvider;
	}

	public void setContextMenuProvider(Supplier<Component> contextMenuProvider) {
		this.contextMenuProvider = contextMenuProvider;
	}

	public void closeContextMenu() {
		queueCommandIfRendered(() -> new UiInfiniteItemView.CloseContextMenuCommand(getId(), this.lastSeenContextMenuRequestId));
	}

	public void reconnect() {
		this.forceRefreshCount++;
		update();
	}

	public UiMediaSoupPublishingParameters getPublishingParameters() {
		return publishingParameters;
	}

	public UiMediaSoupPlaybackParameters getPlaybackParameters() {
		return playbackParameters;
	}
}
