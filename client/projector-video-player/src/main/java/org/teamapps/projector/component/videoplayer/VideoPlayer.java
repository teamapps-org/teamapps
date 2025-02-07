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
package org.teamapps.projector.component.videoplayer;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.DtoComponentConfig;
import org.teamapps.projector.event.ProjectorEvent;

public class VideoPlayer extends AbstractComponent implements DtoVideoPlayerEventHandler {

	private final DtoVideoPlayerClientObjectChannel clientObjectChannel = new DtoVideoPlayerClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<Void> onErrorLoading = new ProjectorEvent<>(clientObjectChannel::toggleErrorLoadingEvent);
	public final ProjectorEvent<Integer> onProgress = new ProjectorEvent<>(clientObjectChannel::togglePlayerProgressEvent);
	public final ProjectorEvent<Void> onEnded = new ProjectorEvent<>(clientObjectChannel::toggleEndedEvent);

	private String url; //the url of the video
	private boolean autoplay; // if set...
	private boolean controlsVisible;
	private String posterImageUrl;
	private PosterImageSize posterImageSize = PosterImageSize.COVER;
	private int playerProgressIntervalSeconds = 1; // if 0, then send NO events
	private Color backgroundColor = new RgbaColor(68, 68, 68);
	private PreloadMode preloadMode = PreloadMode.METADATA;

	public VideoPlayer() {
	}

	public VideoPlayer(String url) {
		this.url = url;
	}

	@Override
	public DtoComponentConfig createDto() {
		DtoVideoPlayer uiVideoPlayer = new DtoVideoPlayer();
		mapAbstractConfigProperties(uiVideoPlayer);
		uiVideoPlayer.setUrl(url);
		uiVideoPlayer.setAutoplay(autoplay);
		uiVideoPlayer.setControlsVisible(controlsVisible);
		uiVideoPlayer.setPosterImageUrl(posterImageUrl);
		uiVideoPlayer.setPosterImageSize(posterImageSize);
		uiVideoPlayer.setPlayerProgressIntervalSeconds(playerProgressIntervalSeconds);
		uiVideoPlayer.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		uiVideoPlayer.setPreloadMode(preloadMode);
		return uiVideoPlayer;
	}

	@Override
	public void handleErrorLoading() {
		onErrorLoading.fire();
	}

	@Override
	public void handlePlayerProgress(int positionInSeconds) {
		onProgress.fire(positionInSeconds);
	}

	@Override
	public void handleEnded() {
		onEnded.fire();
	}

	public void play() {
		clientObjectChannel.play();
	}

	public void pause() {
		clientObjectChannel.pause();
	}

	public void setPosition(int timeInSeconds) {
		clientObjectChannel.jumpTo(timeInSeconds);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		clientObjectChannel.setUrl(url);
	}

	public boolean isAutoplay() {
		return autoplay;
	}

	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
		clientObjectChannel.setAutoplay(autoplay);
	}

	public boolean isControlsVisible() {
		return controlsVisible;
	}

	public void setControlsVisible(boolean controlsVisible) {
		this.controlsVisible = controlsVisible;
		clientObjectChannel.setControlsVisible(controlsVisible);
	}

	public String getPosterImageUrl() {
		return posterImageUrl;
	}

	public void setPosterImageUrl(String posterImageUrl) {
		this.posterImageUrl = posterImageUrl;
		clientObjectChannel.setPosterImageUrl(posterImageUrl);
	}

	public PosterImageSize getPosterImageSize() {
		return posterImageSize;
	}

	public void setPosterImageSize(PosterImageSize posterImageSize) {
		this.posterImageSize = posterImageSize;
		clientObjectChannel.setPosterImageSize(posterImageSize);
	}

	public int getPlayerProgressIntervalSeconds() {
		return playerProgressIntervalSeconds;
	}

	public void setPlayerProgressIntervalSeconds(int playerProgressIntervalSeconds) {
		this.playerProgressIntervalSeconds = playerProgressIntervalSeconds;
		clientObjectChannel.setPlayerProgressIntervalSeconds(playerProgressIntervalSeconds);
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		clientObjectChannel.setBackgroundColor(backgroundColor.toHtmlColorString());
	}

	public PreloadMode getPreloadMode() {
		return preloadMode;
	}

	public void setPreloadMode(PreloadMode preloadMode) {
		this.preloadMode = preloadMode;
		clientObjectChannel.setPreloadMode(preloadMode);
	}

}
