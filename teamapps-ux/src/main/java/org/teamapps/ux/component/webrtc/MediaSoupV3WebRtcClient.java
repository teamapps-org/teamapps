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

	private List<Icon> icons = Collections.emptyList();
	private String caption;
	private String noVideoImageUrl;

	private Double displayAreaAspectRatio; // width / height. Makes the display always use this aspect ratio. If null, use 100% of available space

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
	public UiMediaSoupV3WebRtcClient createUiClientObject() {
		UiMediaSoupV3WebRtcClient ui = new UiMediaSoupV3WebRtcClient();
		mapAbstractUiComponentProperties(ui);
		ui.setPublishingParameters(publishingParameters);
		ui.setPlaybackParameters(playbackParameters);
		ui.setActivityLineVisible(activityLineVisible);
		ui.setActivityInactiveColor(activityInactiveColor != null ? activityInactiveColor.toHtmlColorString() : null);
		ui.setActivityActiveColor(activityActiveColor != null ? activityActiveColor.toHtmlColorString() : null);
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
		if (event instanceof UiMediaSoupV3WebRtcClient.VoiceActivityChangedEvent) {
			UiMediaSoupV3WebRtcClient.VoiceActivityChangedEvent e = (UiMediaSoupV3WebRtcClient.VoiceActivityChangedEvent) event;
			this.onVoiceActivityChanged.fire(e.getActive());

		} else if (event instanceof UiMediaSoupV3WebRtcClient.ClickedEvent) {
			UiMediaSoupV3WebRtcClient.ClickedEvent e = (UiMediaSoupV3WebRtcClient.ClickedEvent) event;
			this.onClicked.fire();

		} else if (event instanceof UiMediaSoupV3WebRtcClient.SourceMediaTrackRetrievalFailedEvent) {
			UiMediaSoupV3WebRtcClient.SourceMediaTrackRetrievalFailedEvent e = (UiMediaSoupV3WebRtcClient.SourceMediaTrackRetrievalFailedEvent) event;
			this.onSourceMediaTrackRetrievalFailed.fire(MediaRetrievalFailureReason.valueOf(e.getReason().name()));

		} else if (event instanceof UiMediaSoupV3WebRtcClient.SourceMediaTrackEndedEvent) {
			UiMediaSoupV3WebRtcClient.SourceMediaTrackEndedEvent e = (UiMediaSoupV3WebRtcClient.SourceMediaTrackEndedEvent) event;
			this.onSourceMediaTrackEnded.fire(SourceMediaTrackType.valueOf(e.getTrackType().name()));

		} else if (event instanceof UiMediaSoupV3WebRtcClient.TrackPublishingSuccessfulEvent) {
			UiMediaSoupV3WebRtcClient.TrackPublishingSuccessfulEvent e = (UiMediaSoupV3WebRtcClient.TrackPublishingSuccessfulEvent) event;
			this.onTrackPublishingSuccessful.fire(new TrackPublishingSuccessfulEventData(e.getAudio(), e.getVideo()));

		} else if (event instanceof UiMediaSoupV3WebRtcClient.TrackPublishingFailedEvent) {
			UiMediaSoupV3WebRtcClient.TrackPublishingFailedEvent e = (UiMediaSoupV3WebRtcClient.TrackPublishingFailedEvent) event;
			this.onTrackPublishingFailed.fire(new TrackPublishingFailedEventData(e.getAudio(), e.getVideo(), e.getErrorMessage()));

		} else if (event instanceof UiMediaSoupV3WebRtcClient.SubscribingSuccessfulEvent) {
			UiMediaSoupV3WebRtcClient.SubscribingSuccessfulEvent e = (UiMediaSoupV3WebRtcClient.SubscribingSuccessfulEvent) event;
			this.onSubscribingSuccessful.fire();

		} else if (event instanceof UiMediaSoupV3WebRtcClient.SubscribingFailedEvent) {
			UiMediaSoupV3WebRtcClient.SubscribingFailedEvent e = (UiMediaSoupV3WebRtcClient.SubscribingFailedEvent) event;
			this.onSubscribingFailed.fire(e.getErrorMessage());

		} else if (event instanceof UiMediaSoupV3WebRtcClient.ConnectionStateChangedEvent) {
			this.onConnectionStateChanged.fire(((UiMediaSoupV3WebRtcClient.ConnectionStateChangedEvent) event).getConnected());

		} else if (event instanceof UiMediaSoupV3WebRtcClient.ContextMenuRequestedEvent) {
			UiMediaSoupV3WebRtcClient.ContextMenuRequestedEvent e = (UiMediaSoupV3WebRtcClient.ContextMenuRequestedEvent) event;
			lastSeenContextMenuRequestId = e.getRequestId();
			if (contextMenuProvider != null) {
				Component contextMenuContent = contextMenuProvider.get();
				if (contextMenuContent != null) {
					queueCommandIfRendered(() -> new UiInfiniteItemView.SetContextMenuContentCommand(e.getRequestId(), contextMenuContent.createUiReference()));
				} else {
					queueCommandIfRendered(() -> new UiInfiniteItemView.CloseContextMenuCommand(e.getRequestId()));
				}
			} else {
				closeContextMenu();
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
		queueCommandIfRendered(() -> new UiMediaSoupV3WebRtcClient.UpdateCommand(createUiClientObject()));
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
			queueCommandIfRendered(() -> new UiMediaSoupV3WebRtcClient.SetActiveCommand(active));
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
			this.icons = icons;
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

	public Double getDisplayAreaAspectRatio() {
		return displayAreaAspectRatio;
	}

	public void setDisplayAreaAspectRatio(Double displayAreaAspectRatio) {
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
		SessionContext.current().sendCommand(null, new UiMediaSoupV3WebRtcClient.EnumerateDevicesCommand(), value -> {
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
		queueCommandIfRendered(() -> new UiInfiniteItemView.CloseContextMenuCommand(this.lastSeenContextMenuRequestId));
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
