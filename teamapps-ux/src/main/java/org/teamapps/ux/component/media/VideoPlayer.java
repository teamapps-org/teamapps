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

import org.teamapps.common.format.RgbaColor;
import org.teamapps.common.format.Color;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiVideoPlayer;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;

public class VideoPlayer extends AbstractComponent {

	public final Event<Void> onErrorLoading = new Event<>();
	public final Event<Integer> onProgress = new Event<>();
	public final Event<Void> onEnded = new Event<>();

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
		UiVideoPlayer uiVideoPlayer = new UiVideoPlayer(url);
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
	public void handleUiEvent(UiEvent event) {
		if (event instanceof UiVideoPlayer.ErrorLoadingEvent) {
			onErrorLoading.fire(null);
		} else if (event instanceof UiVideoPlayer.PlayerProgressEvent) {
			UiVideoPlayer.PlayerProgressEvent e = (UiVideoPlayer.PlayerProgressEvent) event;
			onProgress.fire(e.getPositionInSeconds());
		} else if (event instanceof UiVideoPlayer.EndedEvent) {
			onEnded.fire();
		}
	}

	public void play() {
		queueCommandIfRendered(() -> new UiVideoPlayer.PlayCommand());
	}

	public void pause() {
		queueCommandIfRendered(() -> new UiVideoPlayer.PauseCommand());
	}

	public void setPosition(int timeInSeconds) {
		queueCommandIfRendered(() -> new UiVideoPlayer.JumpToCommand(timeInSeconds));
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		queueCommandIfRendered(() -> new UiVideoPlayer.SetUrlCommand(url));
	}

	public boolean isAutoplay() {
		return autoplay;
	}

	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
		queueCommandIfRendered(() -> new UiVideoPlayer.SetAutoplayCommand(autoplay));
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
		queueCommandIfRendered(() -> new UiVideoPlayer.SetPreloadModeCommand(preloadMode.toUiPreloadMode()));
	}

}
