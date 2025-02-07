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
package org.teamapps.projector.component.mediasoupclient;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.session.SessionContext;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MediaSoupV3WebRtcClient extends AbstractComponent implements DtoMediaSoupV3WebRtcClientEventHandler {

	private final DtoMediaSoupV3WebRtcClientClientObjectChannel clientObjectChannel = new DtoMediaSoupV3WebRtcClientClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<MediaRetrievalFailureReason> onSourceMediaTrackRetrievalFailed = new ProjectorEvent<>(clientObjectChannel::toggleSourceMediaTrackRetrievalFailedEvent);
	public final ProjectorEvent<SourceMediaTrackType> onSourceMediaTrackEnded = new ProjectorEvent<>(clientObjectChannel::toggleSourceMediaTrackEndedEvent);
	public final ProjectorEvent<TrackPublishingSuccessfulEventData> onTrackPublishingSuccessful = new ProjectorEvent<>(clientObjectChannel::toggleTrackPublishingSuccessfulEvent);
	public final ProjectorEvent<TrackPublishingFailedEventData> onTrackPublishingFailed = new ProjectorEvent<>(clientObjectChannel::toggleTrackPublishingFailedEvent);

	public final ProjectorEvent<Void> onSubscribingSuccessful = new ProjectorEvent<>(clientObjectChannel::toggleSubscribingSuccessfulEvent);
	public final ProjectorEvent<String> onSubscribingFailed = new ProjectorEvent<>(clientObjectChannel::toggleSubscribingFailedEvent);

	public final ProjectorEvent<Boolean> onConnectionStateChanged = new ProjectorEvent<>(clientObjectChannel::toggleConnectionStateChangedEvent);
	public final ProjectorEvent<Boolean> onVoiceActivityChanged = new ProjectorEvent<>(clientObjectChannel::toggleVoiceActivityChangedEvent);
	public final ProjectorEvent<Void> onClick = new ProjectorEvent<>(clientObjectChannel::toggleClickEvent);

	private boolean activityLineVisible;
	private Color activityInactiveColor;
	private Color activityActiveColor;
	private boolean active;

	private List<Icon> icons = Collections.emptyList();
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
		mapAbstractConfigProperties(ui);
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
	public void handleSourceMediaTrackRetrievalFailed(MediaRetrievalFailureReason reason) {
		this.onSourceMediaTrackRetrievalFailed.fire(reason);
	}

	@Override
	public void handleSourceMediaTrackEnded(SourceMediaTrackType trackType) {
		this.onSourceMediaTrackEnded.fire(trackType);
	}

	@Override
	public void handleTrackPublishingSuccessful(DtoMediaSoupV3WebRtcClient.TrackPublishingSuccessfulEventWrapper e) {
		this.onTrackPublishingSuccessful.fire(new TrackPublishingSuccessfulEventData(e.isAudio(), e.isVideo()));
	}

	@Override
	public void handleTrackPublishingFailed(DtoMediaSoupV3WebRtcClient.TrackPublishingFailedEventWrapper e) {
		this.onTrackPublishingFailed.fire(new TrackPublishingFailedEventData(e.isAudio(), e.isVideo(), e.getErrorMessage()));
	}

	@Override
	public void handleConnectionStateChanged(boolean connected) {
		this.onConnectionStateChanged.fire(connected);
	}

	@Override
	public void handleSubscribingSuccessful() {
		this.onSubscribingSuccessful.fire();
	}

	@Override
	public void handleSubscribingFailed(String errorMessage) {
		this.onSubscribingFailed.fire(errorMessage);
	}

	@Override
	public void handleSubscriptionPlaybackFailed(String errorMessage) {

	}

	@Override
	public void handleVoiceActivityChanged(boolean active) {
		this.onVoiceActivityChanged.fire(active);
	}

	@Override
	public void handleClick() {
		this.onClick.fire();
	}

	@Override
	public void handleContextMenuRequested(int requestId) {
		lastSeenContextMenuRequestId = requestId;
		if (contextMenuProvider != null) {
			Component contextMenuContent = contextMenuProvider.get();
			if (contextMenuContent != null) {
				clientObjectChannel.setContextMenuContent(requestId, contextMenuContent);
			} else {
				clientObjectChannel.closeContextMenu(requestId);
			}
		} else {
			closeContextMenu();
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
		params.setAudioConstraints(audioConstraints != null ? audioConstraints.createDtoAudioTrackConstraints() : null);
		params.setVideoConstraints(videoConstraints != null ? videoConstraints.createDtoVideoTrackConstraints() : null);
		params.setScreenSharingConstraints(screenSharingConstraints != null ? screenSharingConstraints.createDtoScreenSharingConstraints() : null);
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
		clientObjectChannel.update(this.createDto());
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
			clientObjectChannel.setActive(active);
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

	public static CompletableFuture<List<DtoMediaDeviceInfo>> enumerateDevices() {
		CompletableFuture<List<DtoMediaDeviceInfo>> future = new CompletableFuture<>();
		SessionContext.current().sendStaticCommandWithCallback(MediaSoupV3WebRtcClient.class, DtoMediaSoupV3WebRtcClient.EnumerateDevicesCommand.CMD_NAME, new Object[0], future::complete);
		return future;
	}

	public Supplier<Component> getContextMenuProvider() {
		return contextMenuProvider;
	}

	public void setContextMenuProvider(Supplier<Component> contextMenuProvider) {
		this.contextMenuProvider = contextMenuProvider;
	}

	public void closeContextMenu() {
		clientObjectChannel.closeContextMenu(this.lastSeenContextMenuRequestId);
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
