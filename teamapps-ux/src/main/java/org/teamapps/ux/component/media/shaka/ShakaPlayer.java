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
package org.teamapps.ux.component.media.shaka;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiShakaManifest;
import org.teamapps.dto.UiShakaPlayer;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.media.PosterImageSize;
import org.teamapps.ux.component.media.TrackLabelFormat;
import org.teamapps.ux.session.SessionContext;

public class ShakaPlayer extends AbstractComponent {

	public final ProjectorEvent<Void> onErrorLoading = createProjectorEventBoundToUiEvent(UiShakaPlayer.ErrorLoadingEvent.NAME);
	public final ProjectorEvent<UiShakaManifest> onManifestLoaded = createProjectorEventBoundToUiEvent(UiShakaPlayer.ManifestLoadedEvent.NAME);
	public final ProjectorEvent<Long> onTimeUpdate = createProjectorEventBoundToUiEvent(UiShakaPlayer.TimeUpdateEvent.NAME);
	public final ProjectorEvent<Void> onEnded = createProjectorEventBoundToUiEvent(UiShakaPlayer.EndedEvent.NAME);

	public static void setDistinctManifestAudioTracksFixEnabled(boolean enabled) {
		SessionContext.current().sendStaticCommand(ShakaPlayer.class, new UiShakaPlayer.SetDistinctManifestAudioTracksFixEnabledCommand(enabled));
	}


	private String hlsUrl;
	private String dashUrl;
	private String posterImageUrl;
	private PosterImageSize posterImageSize = PosterImageSize.COVER;
	private int timeUpdateEventThrottleMillis = 1000;
	private Color backgroundColor = Color.BLACK;
	private TrackLabelFormat trackLabelFormat = TrackLabelFormat.LABEL;
	private boolean videoDisabled = false;
	private String audioLanguage;

	private long timeMillis = 0;

	public ShakaPlayer() {
	}

	public ShakaPlayer(String hlsUrl, String dashUrl) {
		this.hlsUrl = hlsUrl;
		this.dashUrl = dashUrl;
	}

	@Override
	public UiComponent createUiClientObject() {
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
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		if (event instanceof UiShakaPlayer.ErrorLoadingEvent) {
			onErrorLoading.fire(null);
		} else if (event instanceof UiShakaPlayer.ManifestLoadedEvent) {
			UiShakaPlayer.ManifestLoadedEvent e = (UiShakaPlayer.ManifestLoadedEvent) event;
			onManifestLoaded.fire(e.getManifest());
		} else if (event instanceof UiShakaPlayer.TimeUpdateEvent) {
			UiShakaPlayer.TimeUpdateEvent e = (UiShakaPlayer.TimeUpdateEvent) event;
			onTimeUpdate.fire(e.getTimeMillis());
			this.timeMillis = e.getTimeMillis();
		} else if (event instanceof UiShakaPlayer.EndedEvent) {
			onEnded.fire();
		}
	}

	public void setTime(long timeMillis) {
		this.timeMillis = timeMillis;
		sendCommandIfRendered(() -> new UiShakaPlayer.SetTimeCommand(timeMillis));
	}

	public long getTime() {
		return timeMillis;
	}

	public void setUrls(String hlsUrl, String dashUrl) {
		this.timeMillis = 0;
		this.hlsUrl = hlsUrl;
		this.dashUrl = dashUrl;
		sendCommandIfRendered(() -> new UiShakaPlayer.SetUrlsCommand(hlsUrl, dashUrl));
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
		this.posterImageUrl = posterImageUrl;
		reRenderIfRendered();
	}

	public PosterImageSize getPosterImageSize() {
		return posterImageSize;
	}

	public void setPosterImageSize(PosterImageSize posterImageSize) {
		this.posterImageSize = posterImageSize;
		reRenderIfRendered();
	}

	public int getTimeUpdateEventThrottleMillis() {
		return timeUpdateEventThrottleMillis;
	}

	public void setTimeUpdateEventThrottleMillis(int timeUpdateEventThrottleMillis) {
		this.timeUpdateEventThrottleMillis = timeUpdateEventThrottleMillis;
		reRenderIfRendered();
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		reRenderIfRendered();
	}

	public TrackLabelFormat getTrackLabelFormat() {
		return trackLabelFormat;
	}

	public void setTrackLabelFormat(TrackLabelFormat trackLabelFormat) {
		this.trackLabelFormat = trackLabelFormat;
		reRenderIfRendered();
	}

	public boolean isVideoDisabled() {
		return videoDisabled;
	}

	public void setVideoDisabled(boolean videoDisabled) {
		this.videoDisabled = videoDisabled;
		reRenderIfRendered();
	}

	public void selectAudioLanguage(String language) {
		selectAudioLanguage(language, null);
	}

	public void selectAudioLanguage(String language, String role) {
		this.audioLanguage = language;
		sendCommandIfRendered(() -> new UiShakaPlayer.SelectAudioLanguageCommand(language, role));
	}
}
