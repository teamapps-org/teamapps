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
package org.teamapps.ux.component.media;

import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.dto.DtoEventWrapper;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.DtoVideoPlayer;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;

public class VideoPlayer extends AbstractComponent {

	public final ProjectorEvent<Void> onErrorLoading = createProjectorEventBoundToUiEvent(DtoVideoPlayer.ErrorLoadingEvent.TYPE_ID);
	public final ProjectorEvent<Integer> onProgress = createProjectorEventBoundToUiEvent(DtoVideoPlayer.PlayerProgressEvent.TYPE_ID);
	public final ProjectorEvent<Void> onEnded = createProjectorEventBoundToUiEvent(DtoVideoPlayer.EndedEvent.TYPE_ID);

	private String url; //the url of the video
	private boolean autoplay; // if set...
	private boolean showControls;
	private String posterImageUrl;
	private PosterImageSize posterImageSize = PosterImageSize.COVER;
	private int sendPlayerProgressEventsEachXSeconds = 1; // if 0, then send NO events
	private Color backgroundColor = new RgbaColor(68, 68, 68);
	private PreloadMode preloadMode = PreloadMode.METADATA;

	public VideoPlayer() {
	}

	public VideoPlayer(String url) {
		this.url = url;
	}

	@Override
	public UiComponent createUiClientObject() {
		DtoVideoPlayer uiVideoPlayer = new DtoVideoPlayer(url);
		mapAbstractUiComponentProperties(uiVideoPlayer);
		uiVideoPlayer.setAutoplay(autoplay);
		uiVideoPlayer.setShowControls(showControls);
		uiVideoPlayer.setPosterImageUrl(posterImageUrl);
		uiVideoPlayer.setPosterImageSize(posterImageSize.toUiPosterImageSize());
		uiVideoPlayer.setSendPlayerProgressEventsEachXSeconds(sendPlayerProgressEventsEachXSeconds);
		uiVideoPlayer.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		uiVideoPlayer.setPreloadMode(preloadMode.toUiPreloadMode());
		return uiVideoPlayer;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoVideoPlayer.ErrorLoadingEvent.TYPE_ID -> {
				var e = event.as(DtoVideoPlayer.ErrorLoadingEventWrapper.class);
				onErrorLoading.fire(null);
			}
			case DtoVideoPlayer.PlayerProgressEvent.TYPE_ID -> {
				var e = event.as(DtoVideoPlayer.PlayerProgressEventWrapper.class);
				onProgress.fire(e.getPositionInSeconds());
			}
			case DtoVideoPlayer.EndedEvent.TYPE_ID -> {
				var e = event.as(DtoVideoPlayer.EndedEventWrapper.class);
				onEnded.fire();
			}
		}
	}

	public void play() {
		sendCommandIfRendered(() -> new DtoVideoPlayer.PlayCommand());
	}

	public void pause() {
		sendCommandIfRendered(() -> new DtoVideoPlayer.PauseCommand());
	}

	public void setPosition(int timeInSeconds) {
		sendCommandIfRendered(() -> new DtoVideoPlayer.JumpToCommand(timeInSeconds));
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		sendCommandIfRendered(() -> new DtoVideoPlayer.SetUrlCommand(url));
	}

	public boolean isAutoplay() {
		return autoplay;
	}

	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
		sendCommandIfRendered(() -> new DtoVideoPlayer.SetAutoplayCommand(autoplay));
	}

	public boolean isShowControls() {
		return showControls;
	}

	public void setShowControls(boolean showControls) {
		this.showControls = showControls;
		reRenderIfRendered();
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

	public int getSendPlayerProgressEventsEachXSeconds() {
		return sendPlayerProgressEventsEachXSeconds;
	}

	public void setSendPlayerProgressEventsEachXSeconds(int sendPlayerProgressEventsEachXSeconds) {
		this.sendPlayerProgressEventsEachXSeconds = sendPlayerProgressEventsEachXSeconds;
		reRenderIfRendered();
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		reRenderIfRendered();
	}

	public PreloadMode getPreloadMode() {
		return preloadMode;
	}

	public void setPreloadMode(PreloadMode preloadMode) {
		this.preloadMode = preloadMode;
		sendCommandIfRendered(() -> new DtoVideoPlayer.SetPreloadModeCommand(preloadMode.toUiPreloadMode()));
	}

}
