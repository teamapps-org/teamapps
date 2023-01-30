/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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


import {UiWaitingVideoInfoConfig} from "../generated/UiWaitingVideoInfoConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiHttpLiveStreamPlayer} from "./live-stream/UiHttpLiveStreamPlayer";
import {UiYoutubePlayer} from "./live-stream/UiYoutubePlayer";
import {UiLiveStreamComPlayer} from "./live-stream/UiLiveStreamComPlayer";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {applyDisplayMode, css, fadeIn, fadeOut, generateUUID, parseHtml} from "./Common";
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


export class UiLiveStreamComponent extends AbstractUiComponent<UiLiveStreamComponentConfig> implements UiLiveStreamComponentCommandHandler, UiLiveStreamComponentEventSource {

	public readonly onResultOfRequestInputDeviceAccess: TeamAppsEvent<UiLiveStreamComponent_ResultOfRequestInputDeviceAccessEvent> = new TeamAppsEvent<UiLiveStreamComponent_ResultOfRequestInputDeviceAccessEvent>();
	public readonly onResultOfRequestInputDeviceInfo: TeamAppsEvent<UiLiveStreamComponent_ResultOfRequestInputDeviceInfoEvent> = new TeamAppsEvent<UiLiveStreamComponent_ResultOfRequestInputDeviceInfoEvent>();

	private $componentWrapper: HTMLElement;

	private $backgroundImageContainer: HTMLElement;
	private $backgroundImage: HTMLElement;

	private $waitingVideoContainer: HTMLElement;
	private waitingVideoPlayer: HTMLVideoElement;
	private waitingVideoInfos: UiWaitingVideoInfoConfig[];
	private currentWaitingVideoIndex: number;

	private $liveStreamPlayerContainer: HTMLElement;
	private hlsPlayer: UiHttpLiveStreamPlayer;
	private liveStreamComPlayer: UiLiveStreamComPlayer;
	private youtubePlayer: UiYoutubePlayer;

	private $imageOverlayContainer: HTMLElement;
	private $imageOverlay: HTMLElement;
	private imageOverlayDisplayMode: UiPageDisplayMode;

	private $infoTextContainer: HTMLElement;

	private volume: number;


	constructor(config: UiLiveStreamComponentConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.volume = config.volume;

		this.$componentWrapper = parseHtml(
			`<div id=${config.id}" class="UiLiveStreamComponent" tabindex="-1">
                    <div class="background-image-container hidden"><img class="background-image"></img></div>
                    <div class="livestream-player-container hidden"></div>
                    <div class="waiting-video-container hidden"><video class="waiting-video-player"></video></div>
                    <div class="image-overlay-container hidden"><img class="image-overlay"></img></div>
                    <div class="info-text-container hidden"></div>
                </div>`);

		this.$backgroundImageContainer = this.$componentWrapper.querySelector<HTMLElement>(':scope .background-image-container');
		this.$backgroundImage = this.$backgroundImageContainer.querySelector<HTMLElement>(':scope img');
		this.$liveStreamPlayerContainer = this.$componentWrapper.querySelector<HTMLElement>(':scope .livestream-player-container');
		this.$waitingVideoContainer = this.$componentWrapper.querySelector<HTMLElement>(':scope .waiting-video-container');
		this.waitingVideoPlayer = <HTMLVideoElement>this.$waitingVideoContainer.querySelector<HTMLElement>(':scope video');
		this.$imageOverlayContainer = this.$componentWrapper.querySelector<HTMLElement>(':scope .image-overlay-container');
		this.$imageOverlay = this.$imageOverlayContainer.querySelector<HTMLElement>(':scope img');
		this.$infoTextContainer = this.$componentWrapper.querySelector<HTMLElement>(':scope .info-text-container');

		this.$backgroundImage.addEventListener("load", () => this.onResize());
		this.$imageOverlay.addEventListener("load", () => this.onResize());

		if (config.backgroundImage) {
			this.$backgroundImage.setAttribute("src", config.backgroundImage);
			this.$backgroundImageContainer.classList.remove("hidden");
		}

		this.waitingVideoPlayer.addEventListener('ended', (e) => {
			this.currentWaitingVideoIndex = (this.currentWaitingVideoIndex + 1) % this.waitingVideoInfos.length;
			this.playWaitingVideo(this.waitingVideoInfos[this.currentWaitingVideoIndex].url, 0);
		});
	}

	public doGetMainElement(): HTMLElement {
		return this.$componentWrapper;
	}

	public onResize(): void {
		this.applyDisplayModes();
		this.updatePlayerSizesAndPositions();
	}

	private applyDisplayModes() {
		if ($(this.$backgroundImage).is(":visible")) {
			applyDisplayMode(this.$backgroundImageContainer, this.$backgroundImage, this._config.backgroundImageDisplayMode);
			$(this.$backgroundImage).position({
				my: "center",
				at: "center",
				of: this.$backgroundImageContainer
			});
		}
		if ($(this.$imageOverlayContainer).is(":visible")) {
			applyDisplayMode(this.$imageOverlayContainer, this.$imageOverlay, this.imageOverlayDisplayMode);
			$(this.$imageOverlay).position({
				my: "center",
				at: "center",
				of: this.$imageOverlayContainer
			});
		}
	}

// TESTING: components.liveStreamPlayer.showWaitingVideos([{url:'Bird-HD.mp4', durationInSeconds: 10}, {url:'Leaf-SD.mp4', durationInSeconds: 25}], 20)
	// components.liveStreamPlayer.stopWaitingVideos()
	public showWaitingVideos(waitingVideoInfoConfig: UiWaitingVideoInfoConfig[], offsetSeconds: number, stopLiveStream: Boolean) {
		if (stopLiveStream) {
			this.stopLiveStream();
		}
		this.waitingVideoInfos = waitingVideoInfoConfig;
		this.$waitingVideoContainer.classList.remove("hidden");
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
		fadeOut(this.$backgroundImageContainer);
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
			fadeIn(this.$backgroundImageContainer);
		}
		$(this.waitingVideoPlayer).animate({volume: 0, opacity: 0}, 1000, 'swing', () => {
			this.waitingVideoPlayer.pause();
			this.$waitingVideoContainer.classList.add("hidden");
		});
	}

	public startHttpLiveStream(url: string) {
		this.stopLiveStream(); // stop all other live streams!
		fadeOut(this.$backgroundImageContainer);

		if (!this.hlsPlayer) {
			this.hlsPlayer = new UiHttpLiveStreamPlayer({
				_type: "UiHttpLiveStreamPlayer",
				id: generateUUID()
			}, this._context);
			this.$liveStreamPlayerContainer.append(this.hlsPlayer.getMainElement());
		}
		this.hlsPlayer.setVolume(this.volume);
		this.hlsPlayer.play(url);

		this.updatePlayerSizesAndPositions();
	}

	public startLiveStreamComLiveStream(url: string) {
		this.stopLiveStream(); // stop all other live streams!
		fadeOut(this.$backgroundImageContainer);

		if (!this.liveStreamComPlayer) {
			this.liveStreamComPlayer = new UiLiveStreamComPlayer({
				_type: "UiLiveStreamComPlayer",
				id: generateUUID()
			}, this._context);
			this.$liveStreamPlayerContainer.append(this.liveStreamComPlayer.getMainElement())
		}
		this.liveStreamComPlayer.getMainElement().classList.remove("hidden");
		this.liveStreamComPlayer.setVolume(this.volume);
		this.liveStreamComPlayer.play(url);

		this.updatePlayerSizesAndPositions();
	}

	// TESTING: components.liveStreamPlayer.startYouTubeLiveStream("https://www.youtube.com/v/K474y2EpHN4")
	public startYouTubeLiveStream(url: string) {
		this.stopLiveStream(); // stop all other live streams!
		fadeOut(this.$backgroundImageContainer);

		if (!this.youtubePlayer) {
			this.youtubePlayer = new UiYoutubePlayer({
				_type: "UiYoutubePlayer",
				id: generateUUID()
			}, this._context);
			this.$liveStreamPlayerContainer.append(this.youtubePlayer.getMainElement());
		}

		this.youtubePlayer.getMainElement().classList.remove("hidden");
		this.youtubePlayer.setVolume(this.volume);
		this.youtubePlayer.play(url);

		this.updatePlayerSizesAndPositions();
	}

	public stopLiveStream() {
		this.doWithAllPlayers(p => {
			p.stop();
		});
		fadeIn(this.$backgroundImageContainer);
		this.updatePlayerSizesAndPositions();
	}

	public displayImageOverlay(imageUrl: string, displayMode: UiPageDisplayMode, useVideoAreaAsFrame: Boolean) {
		this.imageOverlayDisplayMode = displayMode;
		this.$imageOverlay.setAttribute("src", imageUrl);
		this.$imageOverlayContainer.classList.remove("hidden");
	}

	public removeImageOverlay() {
		this.$imageOverlayContainer.classList.add("hidden");
	}

	public displayInfoTextOverlay(text: string) {
		this.$infoTextContainer.textContent = text;
		fadeIn(this.$infoTextContainer);
	}

	public removeInfoTextOverlay() {
		fadeOut(this.$infoTextContainer);
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
				css(p.getMainElement(), {
					top: "0px",
					left: "0px",
					width: "100%",
					height: "100%"
				});
			} else {
				// hide
				css(p.getMainElement(), {
					top: "0px",
					left: "-100000px",
					right: "500px",
					height: "500px"
				});
			}
		});
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiLiveStreamComponent", UiLiveStreamComponent);
