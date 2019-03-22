/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiVideoPlayer;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.common.format.Color;

import static org.teamapps.util.UiUtil.createUiColor;

public class VideoPlayer extends AbstractComponent {

	public final Event<Void> onErrorLoading = new Event<>();
	public final Event<Integer> onVideoPlayerProgress = new Event<>();

	private String url; //the url of the video
	private boolean autoplay; // if set...
	private boolean showControls;
	private String posterImageUrl;
	private PosterImageSize posterImageSize = PosterImageSize.COVER;
	private int sendPlayerProgressEventsEachXSeconds = 1; // if 0, then send NO events
	private Color backgroundColor = new Color(68, 68, 68);
	private PreloadMode preloadMode = PreloadMode.METADATA;

	public VideoPlayer() {
	}

	public VideoPlayer(String url) {
		this.url = url;
	}

	@Override
	public UiComponent createUiComponent() {
		UiVideoPlayer uiVideoPlayer = new UiVideoPlayer(getId(), url);
		mapAbstractUiComponentProperties(uiVideoPlayer);
		uiVideoPlayer.setAutoplay(autoplay);
		uiVideoPlayer.setShowControls(showControls);
		uiVideoPlayer.setPosterImageUrl(posterImageUrl);
		uiVideoPlayer.setPosterImageSize(posterImageSize.toUiPosterImageSize());
		uiVideoPlayer.setSendPlayerProgressEventsEachXSeconds(sendPlayerProgressEventsEachXSeconds);
		uiVideoPlayer.setBackgroundColor(backgroundColor != null ? createUiColor(backgroundColor): null);
		uiVideoPlayer.setPreloadMode(preloadMode.toUiPreloadMode());
		return uiVideoPlayer;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_VIDEO_PLAYER_ERROR_LOADING:
				onErrorLoading.fire(null);
				break;
			case UI_VIDEO_PLAYER_PLAYER_PROGRESS:
				UiVideoPlayer.PlayerProgressEvent progressEvent = (UiVideoPlayer.PlayerProgressEvent) event;
				onVideoPlayerProgress.fire(progressEvent.getPositionInSeconds());
				break;
		}
	}

	public void play() {
		queueCommandIfRendered(() -> new UiVideoPlayer.PlayCommand(getId()));
	}

	public void pause() {
		queueCommandIfRendered(() -> new UiVideoPlayer.PauseCommand(getId()));
	}

	public void setPosition(int timeInSeconds) {
		queueCommandIfRendered(() -> new UiVideoPlayer.JumpToCommand(getId(), timeInSeconds));
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		queueCommandIfRendered(() -> new UiVideoPlayer.SetUrlCommand(getId(), url));
	}

	public boolean isAutoplay() {
		return autoplay;
	}

	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
		queueCommandIfRendered(() -> new UiVideoPlayer.SetAutoplayCommand(getId(), autoplay));
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
		queueCommandIfRendered(() -> new UiVideoPlayer.SetPreloadModeCommand(getId(), preloadMode.toUiPreloadMode()));
	}

	@Override
	protected void doDestroy() {
		// nothing to do
	}
}
