/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.ux.component.media.shaka;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiShakaManifest;
import org.teamapps.dto.UiShakaPlayer;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.media.PosterImageSize;
import org.teamapps.ux.component.media.TrackLabelFormat;
import org.teamapps.ux.session.SessionContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShakaPlayer extends AbstractComponent {

	public final Event<Void> onErrorLoading = new Event<>();
	public final Event<UiShakaManifest> onManifestLoaded = new Event<>();
	public final Event<Long> onTimeUpdate = new Event<>();
	public final Event<Void> onEnded = new Event<>();
	public final Event<SkipClickedEvent> onSkipClicked = new Event<>();
	public static void setDistinctManifestAudioTracksFixEnabled(boolean enabled) {
		SessionContext.current().queueCommand(new UiShakaPlayer.SetDistinctManifestAudioTracksFixEnabledCommand(enabled));
	}

	private String hlsUrl;
	private String dashUrl;
	private String posterImageUrl;
	private PosterImageSize posterImageSize = PosterImageSize.CONTAIN;
	private int timeUpdateEventThrottleMillis = 1000;
	private Color backgroundColor = Color.BLACK;
	private TrackLabelFormat trackLabelFormat = TrackLabelFormat.LABEL;
	private boolean videoDisabled = false;
	private String audioLanguage;
	private boolean bigPlayButtonEnabled = true;
	private int controlFadeDelaySeconds = 0; // 0 = default value (see shaka docs)
	private List<ControlPanelElementType> controlPanelElements = List.of(
			ControlPanelElementType.PLAY_PAUSE,
			ControlPanelElementType.TIME_AND_DURATION,
			ControlPanelElementType.SPACER,
			ControlPanelElementType.MUTE,
			ControlPanelElementType.VOLUME,
			ControlPanelElementType.FULLSCREEN,
			ControlPanelElementType.OVERFLOW_MENU
	);

	private long timeMillis = 0;

	public ShakaPlayer() {
	}

	public ShakaPlayer(String hlsUrl, String dashUrl) {
		this.hlsUrl = hlsUrl;
		this.dashUrl = dashUrl;
	}

	@Override
	public UiComponent createUiComponent() {
		UiShakaPlayer ui = new UiShakaPlayer();
		mapAbstractUiComponentProperties(ui);
		ui.setHlsUrl(hlsUrl);
		ui.setDashUrl(dashUrl);
		ui.setPosterImageUrl(posterImageUrl);
		ui.setPosterImageSize(posterImageSize.toUiPosterImageSize());
		ui.setTimeUpdateEventThrottleMillis(timeUpdateEventThrottleMillis);
		ui.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		ui.setTrackLabelFormat(trackLabelFormat.toUiTrackLabelFormat());
		ui.setVideoDisabled(videoDisabled);
		ui.setTimeMillis(timeMillis);
		ui.setPreferredAudioLanguage(audioLanguage);
		ui.setBigPlayButtonEnabled(bigPlayButtonEnabled);
		ui.setControlFadeDelaySeconds(controlFadeDelaySeconds);
		ui.setControlPanelElements(controlPanelElements.stream().map(t -> t.toUiShakaPlayerControlPanelElementType()).collect(Collectors.toList()));
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_SHAKA_PLAYER_ERROR_LOADING: {
				onErrorLoading.fire(null);
				break;
			}
			case UI_SHAKA_PLAYER_MANIFEST_LOADED: {
				UiShakaPlayer.ManifestLoadedEvent e = (UiShakaPlayer.ManifestLoadedEvent) event;
				onManifestLoaded.fire(e.getManifest());
				break;
			}
			case UI_SHAKA_PLAYER_TIME_UPDATE: {
				UiShakaPlayer.TimeUpdateEvent e = (UiShakaPlayer.TimeUpdateEvent) event;
				onTimeUpdate.fire(e.getTimeMillis());
				this.timeMillis = e.getTimeMillis();
				break;
			}
			case UI_SHAKA_PLAYER_ENDED: {
				onEnded.fire();
				break;
			}
			case UI_SHAKA_PLAYER_SKIP_CLICKED: {
				UiShakaPlayer.SkipClickedEvent e = (UiShakaPlayer.SkipClickedEvent) event;
				onSkipClicked.fire(new SkipClickedEvent(e.getForward(), e.getPlaybackTimeMillis()));
				break;
			}
		}
	}

	public void jumpTo(long timeMillis) {
		this.timeMillis = timeMillis;
		queueCommandIfRendered(() -> new UiShakaPlayer.JumpToCommand(getId(), timeMillis));
	}

	public long getTime() {
		return timeMillis;
	}

	public void setUrls(String hlsUrl, String dashUrl) {
		this.timeMillis = 0;
		this.hlsUrl = hlsUrl;
		this.dashUrl = dashUrl;
		queueCommandIfRendered(() -> new UiShakaPlayer.SetUrlsCommand(getId(), hlsUrl, dashUrl));
	}

	public String getHlsUrl() {
		return hlsUrl;
	}

	public void setHlsUrl(String hlsUrl) {
		setUrls(hlsUrl, dashUrl);
	}

	public String getDashUrl() {
		return dashUrl;
	}

	public void setDashUrl(String dashUrl) {
		setUrls(hlsUrl, dashUrl);
	}

	public String getPosterImageUrl() {
		return posterImageUrl;
	}

	public void setPosterImageUrl(String posterImageUrl) {
		boolean changed = !Objects.equals(posterImageUrl, this.posterImageUrl);
		this.posterImageUrl = posterImageUrl;
		if (changed) {
			reRenderIfRendered();
		}
	}

	public PosterImageSize getPosterImageSize() {
		return posterImageSize;
	}

	public void setPosterImageSize(PosterImageSize posterImageSize) {
		boolean changed = posterImageSize != this.posterImageSize;
		this.posterImageSize = posterImageSize;
		if (changed) {
			reRenderIfRendered();
		}
	}

	public int getTimeUpdateEventThrottleMillis() {
		return timeUpdateEventThrottleMillis;
	}

	public void setTimeUpdateEventThrottleMillis(int timeUpdateEventThrottleMillis) {
		boolean changed = timeUpdateEventThrottleMillis != this.timeUpdateEventThrottleMillis;
		this.timeUpdateEventThrottleMillis = timeUpdateEventThrottleMillis;
		if (changed) {
			reRenderIfRendered();
		}
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		boolean changed = !Objects.equals(backgroundColor, this.backgroundColor);
		this.backgroundColor = backgroundColor;
		if (changed) {
			reRenderIfRendered();
		}
	}

	public TrackLabelFormat getTrackLabelFormat() {
		return trackLabelFormat;
	}

	public void setTrackLabelFormat(TrackLabelFormat trackLabelFormat) {
		boolean changed = trackLabelFormat != this.trackLabelFormat;
		this.trackLabelFormat = trackLabelFormat;
		if (changed) {
			reRenderIfRendered();
		}
	}

	public boolean isVideoDisabled() {
		return videoDisabled;
	}

	public void setVideoDisabled(boolean videoDisabled) {
		boolean changed = videoDisabled != this.videoDisabled;
		this.videoDisabled = videoDisabled;
		if (changed) {
			reRenderIfRendered();
		}
	}

	public void selectAudioLanguage(String language) {
		selectAudioLanguage(language, null);
	}

	public void selectAudioLanguage(String language, String role) {
		this.audioLanguage = language;
		queueCommandIfRendered(() -> new UiShakaPlayer.SelectAudioLanguageCommand(getId(), language, role));
	}

	public boolean isBigPlayButtonEnabled() {
		return bigPlayButtonEnabled;
	}

	public void setBigPlayButtonEnabled(boolean bigPlayButtonEnabled) {
		this.bigPlayButtonEnabled = bigPlayButtonEnabled;
		reRenderIfRendered();
	}

	public int getControlFadeDelaySeconds() {
		return controlFadeDelaySeconds;
	}

	public void setControlFadeDelaySeconds(int controlFadeDelaySeconds) {
		this.controlFadeDelaySeconds = controlFadeDelaySeconds;
		reRenderIfRendered();
	}

	public List<ControlPanelElementType> getControlPanelElements() {
		return controlPanelElements;
	}

	public void setControlPanelElements(List<ControlPanelElementType> controlPanelElements) {
		this.controlPanelElements = controlPanelElements;
		reRenderIfRendered();
	}

	public void play() {
		queueCommandIfRendered(() -> new UiShakaPlayer.PlayCommand(getId()));
	}

	public void pause() {
		queueCommandIfRendered(() -> new UiShakaPlayer.PauseCommand(getId()));
	}
}
