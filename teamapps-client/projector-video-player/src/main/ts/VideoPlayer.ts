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

import "mediaelement/full";
import {AbstractLegacyComponent, parseHtml, removeClassesByFunction, ServerObjectChannel, TeamAppsEvent} from "projector-client-object-api";
import {
	DtoVideoPlayer,
	DtoVideoPlayer_EndedEvent,
	DtoVideoPlayer_ErrorLoadingEvent,
	DtoVideoPlayer_PlayerProgressEvent,
	DtoVideoPlayerCommandHandler,
	DtoVideoPlayerEventSource, PreloadMode, PosterImageSize
} from "./generated";


export class VideoPlayer extends AbstractLegacyComponent<DtoVideoPlayer> implements DtoVideoPlayerCommandHandler, DtoVideoPlayerEventSource {

	public readonly onPlayerProgress: TeamAppsEvent<DtoVideoPlayer_PlayerProgressEvent> = new TeamAppsEvent<DtoVideoPlayer_PlayerProgressEvent>();
	public readonly onEnded: TeamAppsEvent<DtoVideoPlayer_EndedEvent> = new TeamAppsEvent<DtoVideoPlayer_EndedEvent>();
	public readonly onErrorLoading: TeamAppsEvent<DtoVideoPlayer_ErrorLoadingEvent> = new TeamAppsEvent<DtoVideoPlayer_ErrorLoadingEvent>();

	private $componentWrapper: HTMLElement;
	private $video: HTMLVideoElement;
	private mediaPlayer: any;
	private playerInitialized: boolean = false;
	private jumpToPositionWhenReady: number = 0;

	private destroyed: boolean;
	private autoplay: boolean;
	private playState: "initial" | "playing" | "paused" = "initial";

	constructor(config: DtoVideoPlayer, serverObjectChannel: ServerObjectChannel) {
		super(config);

		this.$componentWrapper = parseHtml(
			`<div class="UiVideoPlayer ${config.url == null ? "not-playable" : ""}">
                    <video src="${config.url || ""}" width="100%" height="100%" preload="${(config.preloadMode)}" ${config.autoplay ? "autoplay" : ""}></video>
                </div>`);
		this.$video = this.$componentWrapper.querySelector(":scope video");
		this.setControlsVisible(config.controlsVisible)
		this.setPosterImageUrl(config.posterImageUrl);
		this.setPosterImageSize(config.posterImageSize);

		// TODO this is the point where the element was inserted to the DOM

		this.mediaPlayer = new mejs.MediaElementPlayer(this.$componentWrapper.querySelector<HTMLElement>(':scope video'), {
			enablePluginDebug: false,
			plugins: ['fasterslower'],
			type: '',
			silverlightName: 'silverlightmediaelement.xap',
			features: ['playpause', 'current', 'progress', 'duration', 'tracks', 'volume', 'fullscreen'],
			timerRate: 250,
			success: (mediaElement: HTMLMediaElement) => {
				this.onPlayerInitialized();
				mediaElement.addEventListener('play', (e) => {
					this.onPlayerProgress.fire({
						positionInSeconds: 0
					});
				}, false);
				let lastPlayTime = 0;
				mediaElement.addEventListener('timeupdate', (e) => {
					let currentPlayTime = mediaElement.currentTime;
					if (lastPlayTime < currentPlayTime && lastPlayTime % config.playerProgressIntervalSeconds > currentPlayTime % config.playerProgressIntervalSeconds) {
						this.onPlayerProgress.fire({
							positionInSeconds: Math.floor(currentPlayTime)
						});
					}
					lastPlayTime = currentPlayTime;
				}, false);
				mediaElement.addEventListener('ended', (e) => {
					this.onEnded.fire({});
				});
			},
			error: () => {
				if (!this.destroyed) {
					this.onErrorLoading.fire({})
				}
			}
		});

		this.setBackgroundColor(config.backgroundColor);

		this.setPreloadMode(config.preloadMode);
		this.setAutoplay(config.autoplay);

		this.displayedDeferredExecutor.invokeWhenReady(() => {
			if (this.autoplay) {
				this.play();
			}
		});
	}

	setControlsVisible(controlsVisible: boolean) {
		this.config.controlsVisible = controlsVisible;
		this.$componentWrapper.classList.toggle("hide-controls", !controlsVisible);
    }
    setPosterImageUrl(posterImageUrl: string) {
		this.config.posterImageUrl = posterImageUrl;
		this.$video.poster = posterImageUrl ?? '';
    }
    setPosterImageSize(posterImageSize: PosterImageSize) {
		removeClassesByFunction(this.$componentWrapper.classList, className => className.startsWith("poster-"))
		this.$componentWrapper.classList.add(`poster-${posterImageSize}`);
    }
    setPlayerProgressIntervalSeconds(playerProgressIntervalSeconds: number) {
       	this.config.playerProgressIntervalSeconds = playerProgressIntervalSeconds;
	}
    setBackgroundColor(backgroundColor: string) {
		this.$componentWrapper.style.backgroundColor = backgroundColor;
		this.$componentWrapper.querySelector<HTMLElement>(':scope .mejs__container').style.backgroundColor = backgroundColor;
    }

	public doGetMainElement(): HTMLElement {
		return this.$componentWrapper;
	}

	private onPlayerInitialized() {
		this.playerInitialized = true;
		if (this.playState === "playing" || (this.autoplay && this.playState !== "paused")) {
			this.play();
		}
	}

	public play() {
		this.playState = "playing";
		if (this.playerInitialized) {
			this.mediaPlayer.play();
		}
	}

	public pause() {
		this.mediaPlayer.pause();
		this.playState = "paused";
	}

	public jumpTo(seconds: number) {
		if (this.playerInitialized) {
			this.mediaPlayer.setCurrentTime(seconds);
		} else {
			this.jumpToPositionWhenReady = seconds;
		}
	}

	public onResize(): void {
		// console.log(this.getWidth(), this.getHeight(), Math.min(this.getHeight(), this.mediaPlayer.height), this.mediaPlayer.width, this.mediaPlayer.height)
		// this.mediaPlayer.setPlayerSize(this.getWidth(), Math.min(this.getHeight(), this.mediaPlayer.height)); // CAUTION: maybe we will have to handle fullscreen mode
		this.mediaPlayer.setPlayerSize(this.mediaPlayer.width, this.mediaPlayer.height); // CAUTION: maybe we will have to handle fullscreen mode
		this.mediaPlayer.setControlsSize();
	}

	public destroy(): void {
		super.destroy();
		this.destroyed = true;
		try {
			this.mediaPlayer.pause();
			this.mediaPlayer.setSrc("");
			this.mediaPlayer.load();
		} catch (e) {
			console.error("Error while destroying video player: " + e.toString());
		}
	}

	setAutoplay(autoplay: boolean): void {
		this.autoplay = autoplay;
		this.playState = "initial";

		if (autoplay) {
			if (this.playerInitialized) {
				this.mediaPlayer.play();
			}
			this.$video.setAttribute("autoplay", "autoplay")
		} else {
			this.$video.removeAttribute("autoplay");
		}
	}

	setPreloadMode(preloadMode: PreloadMode): void {
		this.$video.setAttribute("preload", preloadMode);
	}

	setUrl(url: string): void {
		this.getMainElement().querySelector<HTMLElement>(":scope .mejs__overlay-error").parentElement.classList.add("hidden");
		this.getMainElement().querySelector<HTMLElement>(":scope .mejs__poster").classList.remove("hidden");
		this.getMainElement().querySelector<HTMLElement>(":scope .mejs__overlay-play").style.display = "flex";
		this.mediaPlayer.pause();
		if (url == null) {
			this.pause();
		} else {
			this.mediaPlayer.setSrc(url);
		}
		this.$componentWrapper.classList.toggle("not-playable", url == null);
	}
}


