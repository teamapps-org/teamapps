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

import * as $ from "jquery";
import {UiWaitingVideoInfoConfig} from "../generated/UiWaitingVideoInfoConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiHttpLiveStreamPlayer} from "./live-stream/UiHttpLiveStreamPlayer";
import {UiYoutubePlayer} from "./live-stream/UiYoutubePlayer";
import {UiLiveStreamComPlayer} from "./live-stream/UiLiveStreamComPlayer";
import {UiComponent} from "./UiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {applyDisplayMode, generateUUID} from "./Common";
import {LiveStreamPlayer} from "./live-stream/LiveStreamPlayer";
import {
	UiLiveStreamComponent_ResultOfRequestInputDeviceAccessEvent,
	UiLiveStreamComponent_ResultOfRequestInputDeviceInfoEvent,
	UiLiveStreamComponentCommandHandler,
	UiLiveStreamComponentConfig,
	UiLiveStreamComponentEventSource
} from "../generated/UiLiveStreamComponentConfig";
import {UiPageDisplayMode} from "../generated/UiPageDisplayMode";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";


export class UiLiveStreamComponent extends UiComponent<UiLiveStreamComponentConfig> implements UiLiveStreamComponentCommandHandler, UiLiveStreamComponentEventSource {

	public readonly onResultOfRequestInputDeviceAccess: TeamAppsEvent<UiLiveStreamComponent_ResultOfRequestInputDeviceAccessEvent> = new TeamAppsEvent<UiLiveStreamComponent_ResultOfRequestInputDeviceAccessEvent>(this);
	public readonly onResultOfRequestInputDeviceInfo: TeamAppsEvent<UiLiveStreamComponent_ResultOfRequestInputDeviceInfoEvent> = new TeamAppsEvent<UiLiveStreamComponent_ResultOfRequestInputDeviceInfoEvent>(this);

	private $componentWrapper: JQuery;

	private $backgroundImageContainer: JQuery;
	private $backgroundImage: JQuery;

	private $waitingVideoContainer: JQuery;
	private waitingVideoPlayer: HTMLVideoElement;
	private waitingVideoInfos: UiWaitingVideoInfoConfig[];
	private currentWaitingVideoIndex: number;

	private $liveStreamPlayerContainer: JQuery;
	private hlsPlayer: UiHttpLiveStreamPlayer;
	private liveStreamComPlayer: UiLiveStreamComPlayer;
	private youtubePlayer: UiYoutubePlayer;

	private $imageOverlayContainer: JQuery;
	private $imageOverlay: JQuery;
	private imageOverlayDisplayMode: UiPageDisplayMode;

	private $infoTextContainer: JQuery;

	private volume: number;


	constructor(config: UiLiveStreamComponentConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.volume = config.volume;

		this.$componentWrapper = $(
			`<div id=${config.id}" class="UiLiveStreamComponent" tabindex="-1">
                    <div class="background-image-container" style="display: none"><img class="background-image"/></div>
                    <div class="livestream-player-container"></div>
                    <div class="waiting-video-container" style="display: none"><video class="waiting-video-player"></div>
                    <div class="image-overlay-container" style="display: none"><img class="image-overlay"/></div>
                    <div class="info-text-container" style="display: none"></div>
                </div>`);

		this.$backgroundImageContainer = this.$componentWrapper.find('.background-image-container');
		this.$backgroundImage = this.$backgroundImageContainer.find('img');
		this.$liveStreamPlayerContainer = this.$componentWrapper.find('.livestream-player-container');
		this.$waitingVideoContainer = this.$componentWrapper.find('.waiting-video-container');
		this.waitingVideoPlayer = <HTMLVideoElement>this.$waitingVideoContainer.find('video')[0];
		this.$imageOverlayContainer = this.$componentWrapper.find('.image-overlay-container');
		this.$imageOverlay = this.$imageOverlayContainer.find('img');
		this.$infoTextContainer = this.$componentWrapper.find('.info-text-container');

		this.$backgroundImage.on("load", () => this.reLayout());
		this.$imageOverlay.on("load", () => this.reLayout());

		if (config.backgroundImage) {
			this.$backgroundImage.attr("src", config.backgroundImage);
			this.$backgroundImageContainer.show();
		}

		this.waitingVideoPlayer.addEventListener('ended', (e) => {
			this.currentWaitingVideoIndex = (this.currentWaitingVideoIndex + 1) % this.waitingVideoInfos.length;
			this.playWaitingVideo(this.waitingVideoInfos[this.currentWaitingVideoIndex].url, 0);
		});
	}

	public getMainDomElement(): JQuery {
		return this.$componentWrapper;
	}


	protected onAttachedToDom(): void {
		this.reLayout();
	}

	public onResize(): void {
		if (this.$backgroundImage.is(":visible")) {
			applyDisplayMode(this.$backgroundImageContainer, this.$backgroundImage, this._config.backgroundImageDisplayMode);
			this.$backgroundImage.position({
				my: "center",
				at: "center",
				of: this.$backgroundImageContainer
			});
		}
		if (this.$imageOverlayContainer.is(":visible")) {
			applyDisplayMode(this.$imageOverlayContainer, this.$imageOverlay, this.imageOverlayDisplayMode);
			this.$imageOverlay.position({
				my: "center",
				at: "center",
				of: this.$imageOverlayContainer
			});
		}
		this.updatePlayerSizesAndPositions();
	}

	// TESTING: components.liveStreamPlayer.showWaitingVideos([{url:'Bird-HD.mp4', durationInSeconds: 10}, {url:'Leaf-SD.mp4', durationInSeconds: 25}], 20)
	// components.liveStreamPlayer.stopWaitingVideos()
	public showWaitingVideos(waitingVideoInfoConfig: UiWaitingVideoInfoConfig[], offsetSeconds: number, stopLiveStream: Boolean) {
		if (stopLiveStream) {
			this.stopLiveStream();
		}
		this.waitingVideoInfos = waitingVideoInfoConfig;
		this.$waitingVideoContainer.show();
		this.currentWaitingVideoIndex = 0;
		let offset = Math.ceil(offsetSeconds);

		if (offset > 0) {
			let totalMusicPlaylistDuration = waitingVideoInfoConfig.reduce((p, info) => p + info.durationInSeconds, 0);
			offset = offset % totalMusicPlaylistDuration;

			for (; this.currentWaitingVideoIndex < waitingVideoInfoConfig.length; this.currentWaitingVideoIndex++) {
				var videoInfo = waitingVideoInfoConfig[this.currentWaitingVideoIndex];
				if (offset - videoInfo.durationInSeconds < 0) {
					break; // we have reached the current video
				} else {
					offset -= videoInfo.durationInSeconds;
				}
			}
		}

		this.playWaitingVideo(this.waitingVideoInfos[this.currentWaitingVideoIndex].url, offset);
	}

	private playWaitingVideo(videoUrl: string, offsetSeconds: number) {
		this.$backgroundImageContainer.fadeOut(1000);
		this.waitingVideoPlayer.volume = this.volume;
		this.waitingVideoPlayer.style.opacity = "1";
		this.waitingVideoPlayer.src = videoUrl;
		this.waitingVideoPlayer.load();
		this.waitingVideoPlayer.addEventListener('loadedmetadata', () => {
			this.waitingVideoPlayer.currentTime = offsetSeconds;
		}, false);
		this.waitingVideoPlayer.play();
	}

	public stopWaitingVideos() {
		if (this._config.backgroundImage) {
			this.$backgroundImageContainer.fadeIn(1000);
		}
		$(this.waitingVideoPlayer).animate({volume: 0, opacity: 0}, 1000, 'swing', () => {
			this.waitingVideoPlayer.pause();
			this.$waitingVideoContainer.hide();
		});
	}

	public startHttpLiveStream(url: string) {
		this.stopLiveStream(); // stop all other live streams!
		this.$backgroundImageContainer.fadeOut(1000);

		if (!this.hlsPlayer) {
			this.hlsPlayer = new UiHttpLiveStreamPlayer({
				_type: "UiHttpLiveStreamPlayer",
				id: generateUUID()
			}, this._context);
			this.$liveStreamPlayerContainer.append(this.hlsPlayer.getMainDomElement());
		}
		this.hlsPlayer.setVolume(this.volume);
		this.hlsPlayer.play(url);

		this.updatePlayerSizesAndPositions();
	}

	public startLiveStreamComLiveStream(url: string) {
		this.stopLiveStream(); // stop all other live streams!
		this.$backgroundImageContainer.fadeOut(1000);

		if (!this.liveStreamComPlayer) {
			this.liveStreamComPlayer = new UiLiveStreamComPlayer({
				_type: "UiLiveStreamComPlayer",
				id: generateUUID()
			}, this._context);
			this.$liveStreamPlayerContainer.append(this.liveStreamComPlayer.getMainDomElement())
		}
		this.liveStreamComPlayer.getMainDomElement().show();
		this.liveStreamComPlayer.setVolume(this.volume);
		this.liveStreamComPlayer.play(url);

		this.updatePlayerSizesAndPositions();
	}

	// TESTING: components.liveStreamPlayer.startYouTubeLiveStream("https://www.youtube.com/v/K474y2EpHN4")
	public startYouTubeLiveStream(url: string) {
		this.stopLiveStream(); // stop all other live streams!
		this.$backgroundImageContainer.fadeOut(1000);

		if (!this.youtubePlayer) {
			this.youtubePlayer = new UiYoutubePlayer({
				_type: "UiYoutubePlayer",
				id: generateUUID()
			}, this._context);
			this.$liveStreamPlayerContainer.append(this.youtubePlayer.getMainDomElement());
		}

		this.youtubePlayer.getMainDomElement().show();
		this.youtubePlayer.setVolume(this.volume);
		this.youtubePlayer.play(url);

		this.updatePlayerSizesAndPositions();
	}

	public stopLiveStream() {
		this.doWithAllPlayers(p => {
			p.stop();
		});
		this.$backgroundImageContainer.fadeIn(1000);
		this.reLayout();

		this.updatePlayerSizesAndPositions();
	}

	public displayImageOverlay(imageUrl: string, displayMode: UiPageDisplayMode, useVideoAreaAsFrame: Boolean) {
		this.imageOverlayDisplayMode = displayMode;
		this.$imageOverlay.attr("src", imageUrl);
		this.$imageOverlayContainer.show();
		this.reLayout();
	}

	public removeImageOverlay() {
		this.$imageOverlayContainer.hide();
	}

	public displayInfoTextOverlay(text: string) {
		this.$infoTextContainer
			.text(text)
			.fadeIn(1000);
	}

	public removeInfoTextOverlay() {
		this.$infoTextContainer.fadeOut(1000);
	}

	startCustomEmbeddedLiveStreamPlayer(playerEmbedHtml: string, embedContainerId: string): void {
		this.logger.error("TODO: startCustomEmbeddedLiveStreamPlayer() not yet implemented"); // TODO startCustomEmbeddedLiveStreamPlayer()
	}

	requestInputDeviceInfo(): void {
		this.logger.error("TODO: requestInputDeviceInfo() not yet implemented"); // TODO requestInputDeviceInfo()
	}

	public setVolume(volume: number) {
		if (this.waitingVideoPlayer) {
			this.waitingVideoPlayer.volume = volume;
		}
		this.doWithAllPlayers(p => p.setVolume(volume));
	}

	private doWithAllPlayers(f: (p: LiveStreamPlayer) => void, except?: LiveStreamPlayer) {
		let players = [this.liveStreamComPlayer, this.hlsPlayer, this.youtubePlayer];
		players
			.filter(p => p != null && p != except)
			.forEach(p => f(p));
	}

	private updatePlayerSizesAndPositions() {
		this.doWithAllPlayers(p => {
			if (p.isPlaying()) {
				// full size
				p.getMainDomElement().css({
					top: "0px",
					left: "0px",
					width: "100%",
					height: "100%"
				});
			} else {
				// hide
				p.getMainDomElement().css({
					top: "0px",
					left: "-100000px",
					right: "500px",
					height: "500px"
				});
			}
		});
	}

	public destroy(): void {
		// nothing to do as far as I know...
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiLiveStreamComponent", UiLiveStreamComponent);
