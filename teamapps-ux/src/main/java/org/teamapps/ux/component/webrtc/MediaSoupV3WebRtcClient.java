/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.teamapps.common.format.Color;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiInfiniteItemView;
import org.teamapps.dto.UiMediaDeviceInfo;
import org.teamapps.dto.UiMediaSoupPlaybackParameters;
import org.teamapps.dto.UiMediaSoupPublishingParameters;
import org.teamapps.dto.UiMediaSoupV3WebRtcClient;
import org.teamapps.dto.WebRtcPublishingFailureReason;
import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;
import org.teamapps.util.UiUtil;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.session.SessionContext;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MediaSoupV3WebRtcClient extends AbstractComponent {

	public final Event<MulticastPlaybackProfile> onPlaybackProfileChanged = new Event<>();
	public final Event<Boolean> onVoiceActivityChanged = new Event<>();
	public final Event<Void> onClicked = new Event<>();
	public final Event<Void> onPublishingSucceeded = new Event<>();
	public final Event<PublishedStreamsStatus> onPublishedStreamsStatusChanged = new Event<>();
	public final Event<WebRtcPublishingFailureReason> onPublishingFailed = new Event<>();
	public final Event<WebRtcStreamType> onPublishedStreamEnded = new Event<>();
	public final Event<Void> onPlaybackSucceeded = new Event<>();
	public final Event<Void> onPlaybackFailed = new Event<>();
	public final Event<Boolean> onConnectionStateChanged = new Event<>();

	private String serverAddress;

	private boolean activityLineVisible;
	private Color activityInactiveColor;
	private Color activityActiveColor;
	private boolean active;

	private List<Icon> icons = Collections.emptyList();
	private String caption;
	private String noVideoImageUrl;

	private Float displayAreaAspectRatio; // width / height. Makes the display always use this aspect ratio. If null, use 100% of available space

	private double playbackVolume = 1;

	private UiMediaSoupPublishingParameters publishingParameters;
	private UiMediaSoupPlaybackParameters playbackParameters;

	private Supplier<Component> contextMenuProvider = null;
	private int lastSeenContextMenuRequestId;

	public MediaSoupV3WebRtcClient() {
	}

	public MediaSoupV3WebRtcClient(String serverUrl) {
		this.serverAddress = serverUrl;
	}

	@Override
	public UiMediaSoupV3WebRtcClient createUiComponent() {
		UiMediaSoupV3WebRtcClient ui = new UiMediaSoupV3WebRtcClient();
		mapAbstractUiComponentProperties(ui);
		ui.setPublishingParameters(publishingParameters);
		ui.setPlaybackParameters(playbackParameters);
		ui.setActivityLineVisible(activityLineVisible);
		ui.setActivityInactiveColor(UiUtil.createUiColor(activityInactiveColor));
		ui.setActivityActiveColor(UiUtil.createUiColor(activityActiveColor));
		ui.setIcons(icons.stream().map(icon -> getSessionContext().resolveIcon(icon)).collect(Collectors.toList()));
		ui.setCaption(caption);
		ui.setNoVideoImageUrl(noVideoImageUrl);
		ui.setDisplayAreaAspectRatio(displayAreaAspectRatio);
		ui.setPlaybackVolume(playbackVolume);
		ui.setContextMenuEnabled(contextMenuProvider != null);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_PLAYBACK_PROFILE_CHANGED: {
				UiMediaSoupV3WebRtcClient.PlaybackProfileChangedEvent e = (UiMediaSoupV3WebRtcClient.PlaybackProfileChangedEvent) event;
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
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_PUBLISHING_SUCCEEDED:
				this.onPublishingSucceeded.fire();
				break;
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_PUBLISHED_STREAMS_STATUS_CHANGED: {
				UiMediaSoupV3WebRtcClient.PublishedStreamsStatusChangedEvent e = (UiMediaSoupV3WebRtcClient.PublishedStreamsStatusChangedEvent) event;
				this.onPublishedStreamsStatusChanged.fire(new PublishedStreamsStatus(e.getAudio(), e.getVideo()));
				break;
			}
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_PUBLISHING_FAILED:
				this.onPublishingFailed.fire(((UiMediaSoupV3WebRtcClient.PublishingFailedEvent) event).getReason());
				break;
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_PUBLISHED_STREAM_ENDED:
				this.onPublishedStreamEnded.fire(((UiMediaSoupV3WebRtcClient.PublishedStreamEndedEvent) event).getIsDisplay() ? WebRtcStreamType.DISPLAY : WebRtcStreamType.CAM_MIC);
				break;
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_PLAYBACK_SUCCEEDED:
				this.onPlaybackSucceeded.fire();
				break;
			case UI_MEDIA_SOUP_V3_WEB_RTC_CLIENT_PLAYBACK_FAILED:
				this.onPlaybackFailed.fire();
				break;
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

	public void publish(String uid,
	                    String token,
	                    AudioTrackConstraints audioConstraints,
	                    VideoTrackConstraints videoConstraints,
	                    ScreenSharingConstraints screenSharingConstraints,
	                    long maxBitrate,
	                    boolean simulcast
	) {
		UiMediaSoupPublishingParameters params = new UiMediaSoupPublishingParameters();
		params.setServerAddress(serverAddress);
		params.setUid(uid);
		params.setToken(token);
		params.setAudioConstraints(audioConstraints != null ? audioConstraints.createUiAudioTrackConstraints() : null);
		params.setVideoConstraints(videoConstraints != null ? videoConstraints.createUiVideoTrackConstraints() : null);
		params.setScreenSharingConstraints(screenSharingConstraints != null ? screenSharingConstraints.createUiScreenSharingConstraints() : null);
		params.setMaxBitrate(maxBitrate);
		params.setSimulcast(simulcast);
		this.publishingParameters = params;
		update();
	}

	public void play(String uid, String token, boolean audio, boolean video, long minBitrate, long maxBitrate) {
		UiMediaSoupPlaybackParameters params = new UiMediaSoupPlaybackParameters();
		params.setServerAddress(serverAddress);
		params.setUid(uid);
		params.setToken(token);
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

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
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

	public static String generatePublishJwtToken(String streamUuid, String secret, Duration tokenValidityDuration) {
		return generateJwtToken(streamUuid, secret, tokenValidityDuration, "1" /*PUBLISH*/);
	}

	public static String generateSubscribeJwtToken(String streamUuid, String secret, Duration tokenValidityDuration) {
		return generateJwtToken(streamUuid, secret, tokenValidityDuration, "0" /*SUBSCRIBE*/);
	}

	private static String generateJwtToken(String streamUuid, String secret, Duration tokenValidityDuration, String operation) {
		if (secret == null) {
			return "";
		}
		try {
			Algorithm algorithm = Algorithm.HMAC512(secret);
			return JWT.create()
					.withClaim("stream", streamUuid)
					.withClaim("operation", operation)
					.withExpiresAt(new Date(Instant.now().plus(tokenValidityDuration).toEpochMilli()))
					.sign(algorithm);
		} catch (JWTCreationException exception){
			throw new RuntimeException("Could not create auth token - this should never happen!");
		}
	}}
