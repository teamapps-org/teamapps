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
package org.teamapps.projector.component.common.webrtc;

import org.teamapps.common.format.Color;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.projector.event.ProjectorEvent;
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

	public final ProjectorEvent<MediaRetrievalFailureReason> onSourceMediaTrackRetrievalFailed = new ProjectorEvent<>(clientObjectChannel::toggleSourceMediaTrackRetrievalFailedEvent);
	public final ProjectorEvent<SourceMediaTrackType> onSourceMediaTrackEnded = new ProjectorEvent<>(clientObjectChannel::toggleSourceMediaTrackEndedEvent);
	public final ProjectorEvent<TrackPublishingSuccessfulEventData> onTrackPublishingSuccessful = new ProjectorEvent<>(clientObjectChannel::toggleTrackPublishingSuccessfulEvent);
	public final ProjectorEvent<TrackPublishingFailedEventData> onTrackPublishingFailed = new ProjectorEvent<>(clientObjectChannel::toggleTrackPublishingFailedEvent);

	public final ProjectorEvent<Void> onSubscribingSuccessful = new ProjectorEvent<>(clientObjectChannel::toggleSubscribingSuccessfulEvent);
	public final ProjectorEvent<String> onSubscribingFailed = new ProjectorEvent<>(clientObjectChannel::toggleSubscribingFailedEvent);

	public final ProjectorEvent<Boolean> onConnectionStateChanged = new ProjectorEvent<>(clientObjectChannel::toggleConnectionStateChangedEvent);
	public final ProjectorEvent<Boolean> onVoiceActivityChanged = new ProjectorEvent<>(clientObjectChannel::toggleVoiceActivityChangedEvent);
	public final ProjectorEvent<Void> onClicked = new ProjectorEvent<>(clientObjectChannel::toggleClickedEvent);

	private boolean activityLineVisible;
	private Color activityInactiveColor;
	private Color activityActiveColor;
	private boolean active;

	private List<Icon<?, ?>> icons = Collections.emptyList();
	private String caption;
	private String noVideoImageUrl;

	private Double displayAreaAspectRatio; // width / height. Makes the display always use this aspect ratio. If null, use 100% of available space

	private double playbackVolume = 1;

	private DtoMediaSoupPublishingParameters publishingParameters;
	private DtoMediaSoupPlaybackParameters playbackParameters;

	private Supplier<Component> contextMenuProvider = null;
	private int lastSeenContextMenuRequestId;

	private boolean bitrateDisplayEnabled;

	private int forceRefreshCount = 0;

	public MediaSoupV3WebRtcClient() {
	}

	@Override
	public DtoMediaSoupV3WebRtcClient createDto() {
		DtoMediaSoupV3WebRtcClient ui = new DtoMediaSoupV3WebRtcClient();
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
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoMediaSoupV3WebRtcClient.VoiceActivityChangedEvent.TYPE_ID -> {
				var e = event.as(DtoMediaSoupV3WebRtcClient.VoiceActivityChangedEventWrapper.class);
				this.onVoiceActivityChanged.fire(e.getActive());

			}
			case DtoMediaSoupV3WebRtcClient.ClickedEvent.TYPE_ID -> {
				var e = event.as(DtoMediaSoupV3WebRtcClient.ClickedEventWrapper.class);
				this.onClicked.fire();

			}
			case DtoMediaSoupV3WebRtcClient.SourceMediaTrackRetrievalFailedEvent.TYPE_ID -> {
				var e = event.as(DtoMediaSoupV3WebRtcClient.SourceMediaTrackRetrievalFailedEventWrapper.class);
				this.onSourceMediaTrackRetrievalFailed.fire(MediaRetrievalFailureReason.valueOf(e.getReason().name()));

			}
			case DtoMediaSoupV3WebRtcClient.SourceMediaTrackEndedEvent.TYPE_ID -> {
				var e = event.as(DtoMediaSoupV3WebRtcClient.SourceMediaTrackEndedEventWrapper.class);
				this.onSourceMediaTrackEnded.fire(SourceMediaTrackType.valueOf(e.getTrackType().name()));

			}
			case DtoMediaSoupV3WebRtcClient.TrackPublishingSuccessfulEvent.TYPE_ID -> {
				var e = event.as(DtoMediaSoupV3WebRtcClient.TrackPublishingSuccessfulEventWrapper.class);
				this.onTrackPublishingSuccessful.fire(new TrackPublishingSuccessfulEventData(e.getAudio(), e.getVideo()));

			}
			case DtoMediaSoupV3WebRtcClient.TrackPublishingFailedEvent.TYPE_ID -> {
				var e = event.as(DtoMediaSoupV3WebRtcClient.TrackPublishingFailedEventWrapper.class);
				this.onTrackPublishingFailed.fire(new TrackPublishingFailedEventData(e.getAudio(), e.getVideo(), e.getErrorMessage()));

			}
			case DtoMediaSoupV3WebRtcClient.SubscribingSuccessfulEvent.TYPE_ID -> {
				var e = event.as(DtoMediaSoupV3WebRtcClient.SubscribingSuccessfulEventWrapper.class);
				this.onSubscribingSuccessful.fire();

			}
			case DtoMediaSoupV3WebRtcClient.SubscribingFailedEvent.TYPE_ID -> {
				var e = event.as(DtoMediaSoupV3WebRtcClient.SubscribingFailedEventWrapper.class);
				this.onSubscribingFailed.fire(e.getErrorMessage());

			}
			case DtoMediaSoupV3WebRtcClient.ConnectionStateChangedEvent.TYPE_ID -> {
				var e = event.as(DtoMediaSoupV3WebRtcClient.ConnectionStateChangedEventWrapper.class);
				this.onConnectionStateChanged.fire(e.getConnected());

			}
			case DtoMediaSoupV3WebRtcClient.ContextMenuRequestedEvent.TYPE_ID -> {
				var e = event.as(DtoMediaSoupV3WebRtcClient.ContextMenuRequestedEventWrapper.class);
				lastSeenContextMenuRequestId = e.getRequestId();
				if (contextMenuProvider != null) {
					Component contextMenuContent = contextMenuProvider.get();
					if (contextMenuContent != null) {
						clientObjectChannel.setContextMenuContent(E.GetRequestId(), contextMenuContent.createDtoReference());
					} else {
						clientObjectChannel.closeContextMenu(E.GetRequestId());
					}
				} else {
					closeContextMenu();
				}

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
		DtoMediaSoupPublishingParameters params = new DtoMediaSoupPublishingParameters();
		params.setServer(new DtoMediaServerUrlAndToken(serverUrl, worker, token));
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
		DtoMediaSoupPlaybackParameters params = new DtoMediaSoupPlaybackParameters();
		params.setStreamUuid(streamUuid);
		params.setServer(new DtoMediaServerUrlAndToken(server.getServerUrl(), server.getWorker(), server.getToken()));
		params.setOrigin(origin != null ? new DtoMediaServerUrlAndToken(origin.getServerUrl(), origin.getWorker(), origin.getToken()) : null);
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
		clientObjectChannel.update(CreateDto());
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
			clientObjectChannel.setActive(Active);
		}
	}

	public List<Icon<?, ?>> getIcons() {
		return icons;
	}

	public void setIcons(List<Icon<?, ?>> icons) {
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

	public static CompletableFuture<List<DtoMediaDeviceInfo>> enumerateDevices() {
		CompletableFuture<List<DtoMediaDeviceInfo>> future = new CompletableFuture<>();
		SessionContext.current().sendStaticCommand(MediaSoupV3WebRtcClient.class, new DtoMediaSoupV3WebRtcClient.EnumerateDevicesCommand(), value -> {
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
		clientObjectChannel.closeContextMenu(This.LastSeenContextMenuRequestId);
	}

	public void reconnect() {
		this.forceRefreshCount++;
		update();
	}

	public DtoMediaSoupPublishingParameters getPublishingParameters() {
		return publishingParameters;
	}

	public DtoMediaSoupPlaybackParameters getPlaybackParameters() {
		return playbackParameters;
	}
}
