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

import {AbstractUiComponent} from "../../AbstractUiComponent";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {arraysEqual, calculateDisplayModeInnerSize, parseHtml, removeClassesByFunction} from "../../Common";
import {
	UiMediaSoupV2WebRtcClient_ClickedEvent,
	UiMediaSoupV2WebRtcClient_ConnectionStateChangedEvent,
	UiMediaSoupV2WebRtcClient_ContextMenuRequestedEvent,
	UiMediaSoupV2WebRtcClient_PlaybackFailedEvent,
	UiMediaSoupV2WebRtcClient_PlaybackProfileChangedEvent,
	UiMediaSoupV2WebRtcClient_PlaybackSucceededEvent,
	UiMediaSoupV2WebRtcClient_PublishedStreamEndedEvent,
	UiMediaSoupV2WebRtcClient_PublishedStreamsStatusChangedEvent,
	UiMediaSoupV2WebRtcClient_PublishingFailedEvent,
	UiMediaSoupV2WebRtcClient_PublishingSucceededEvent,
	UiMediaSoupV2WebRtcClient_VoiceActivityChangedEvent,
	UiMediaSoupV2WebRtcClientCommandHandler,
	UiMediaSoupV2WebRtcClientConfig,
	UiMediaSoupV2WebRtcClientEventSource
} from "../../../generated/UiMediaSoupV2WebRtcClientConfig";
import {Conference} from "./lib/conference";
import {UiMediaSoupPlaybackParametersConfig} from "../../../generated/UiMediaSoupPlaybackParametersConfig";
import {UiMediaSoupPublishingParametersConfig} from "../../../generated/UiMediaSoupPublishingParametersConfig";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {UiMulticastPlaybackProfile} from "../../../generated/UiMulticastPlaybackProfile";
import {createUiColorCssString} from "../../util/CssFormatUtil";
import {UiPageDisplayMode} from "../../../generated/UiPageDisplayMode";
import {WebRtcPublishingFailureReason} from "../../../generated/WebRtcPublishingFailureReason";
import {ContextMenu} from "../../micro-components/ContextMenu";
import {UiComponent} from "../../UiComponent";
import {Postponer} from "../../util/postpone";
import {addVoiceActivityDetection, enumerateDevices, retrieveUserMedia} from "../MediaUtil";
import {UiMediaDeviceInfoConfig} from "../../../generated/UiMediaDeviceInfoConfig";

export class UiMediaSoupV2WebRtcClient extends AbstractUiComponent<UiMediaSoupV2WebRtcClientConfig> implements UiMediaSoupV2WebRtcClientCommandHandler, UiMediaSoupV2WebRtcClientEventSource {
	public readonly onPublishingSucceeded: TeamAppsEvent<UiMediaSoupV2WebRtcClient_PublishingSucceededEvent> = new TeamAppsEvent(this);
	public readonly onPublishingFailed: TeamAppsEvent<UiMediaSoupV2WebRtcClient_PublishingFailedEvent> = new TeamAppsEvent(this);
	public readonly onPublishedStreamEnded: TeamAppsEvent<UiMediaSoupV2WebRtcClient_PublishedStreamEndedEvent> = new TeamAppsEvent(this);
	public readonly onPublishedStreamsStatusChanged: TeamAppsEvent<UiMediaSoupV2WebRtcClient_PublishedStreamsStatusChangedEvent> = new TeamAppsEvent(this);
	public readonly onConnectionStateChanged: TeamAppsEvent<UiMediaSoupV2WebRtcClient_ConnectionStateChangedEvent> = new TeamAppsEvent(this);
	public readonly onPlaybackSucceeded: TeamAppsEvent<UiMediaSoupV2WebRtcClient_PlaybackSucceededEvent> = new TeamAppsEvent(this);
	public readonly onPlaybackFailed: TeamAppsEvent<UiMediaSoupV2WebRtcClient_PlaybackFailedEvent> = new TeamAppsEvent(this);
	public readonly onPlaybackProfileChanged: TeamAppsEvent<UiMediaSoupV2WebRtcClient_PlaybackProfileChangedEvent> = new TeamAppsEvent(this);
	public readonly onVoiceActivityChanged: TeamAppsEvent<UiMediaSoupV2WebRtcClient_VoiceActivityChangedEvent> = new TeamAppsEvent(this);
	public readonly onClicked: TeamAppsEvent<UiMediaSoupV2WebRtcClient_ClickedEvent> = new TeamAppsEvent(this);
	public readonly onContextMenuRequested: TeamAppsEvent<UiMediaSoupV2WebRtcClient_ContextMenuRequestedEvent> = new TeamAppsEvent(this);

	private $main: HTMLDivElement;
	private conference: Conference;
	private $image: HTMLImageElement;
	private $videoContainer: HTMLElement;
	private $video: HTMLVideoElement;
	private $profileDisplay: HTMLElement;
	private $icons: HTMLImageElement;
	private $caption: HTMLElement;
	private $spinner: HTMLElement;
	private currentSourceStreams: MediaStream[];

	private connectionStatus: {
		status: "idle" | "publish" | "playback" | "error",
		connected: boolean,
		audioStatus: boolean,
		audioBitrate: number // assume sending/receiving from the beginning
	} = {
		status: "idle",
		connected: false,
		audioStatus: false,
		audioBitrate: -1
	};

	private contextMenu: ContextMenu;

	constructor(config: UiMediaSoupV2WebRtcClientConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiMediaSoupV2WebRtcClient state-idle">
	<div class="video-container">
		<img class="image"></img>
		<video class="video" playsinline muted></video>
		<div class="icons"></div>
		<div class="spinner-wrapper">
			<div class="spinner teamapps-spinner"></div>
		</div>
	</div>
	<div class="caption"></div>
	<div class="profile hidden">.</div>
</div>`);
		this.$image = this.$main.querySelector(":scope .image");
		this.$videoContainer = this.$main.querySelector(":scope .video-container");
		this.$video = this.$main.querySelector<HTMLVideoElement>(":scope .video");
		this.$profileDisplay = this.$main.querySelector(":scope .profile");
		this.$icons = this.$main.querySelector(":scope .icons");
		this.$caption = this.$main.querySelector(":scope .caption");
		this.$spinner = this.$main.querySelector(":scope .spinner");

		this.contextMenu = new ContextMenu();

		[this.$videoContainer, this.$caption].forEach($element => $element.addEventListener("click", () => {
			this.onClicked.fire({})
		}));
		[this.$videoContainer, this.$caption].forEach($element => $element.addEventListener("contextmenu", (e) => {
			if (this._config.contextMenuEnabled) {
				this.contextMenu.open(e, requestId => this.onContextMenuRequested.fire({requestId}))
			}
		}));
		this.$image.addEventListener("load", () => {
			console.log("load");
			this.onResize()
		});
		this.$video.addEventListener("play", ev => {
			console.log("play");
			this.update(this._config)
		});
		this.$video.addEventListener("playing", ev => {
			console.log("playing");
			this.update(this._config)
		});
		this.$video.addEventListener("pause", ev => {
			console.log("pause");
			this.$video.play(); // happens when the video player gets detached under android while switching views
		});
		this.$video.addEventListener("stalled", ev => {
			console.log("stalled");
			this.update(this._config)
		});
		this.$video.addEventListener("waiting", ev => {
			console.log("waiting");
			this.update(this._config)
		});
		this.$video.addEventListener("ended", ev => {
			console.log("ended");
			this.update(this._config)
		});
		this.$video.addEventListener("suspend", ev => {
			console.log("suspend");
			this.update(this._config)
		});
		this.$video.addEventListener("abort", ev => {
			console.log("abort");
			this.update(this._config)
		});
		this.$video.addEventListener("progress", ev => {
			// console.log("progress");
			this.updateVideoVisibility();
			this.onResize()
		});
		this.$video.addEventListener("timeupdate", ev => {
			// console.log("timeupdate");
			this.updateVideoVisibility();
			this.onResize()
		});

		this.update(config);

		if (config.initialPlaybackOrPublishParams != null) {
			if (config.initialPlaybackOrPublishParams._type === 'UiMediaSoupPlaybackParamaters') {
				this.playback(config.initialPlaybackOrPublishParams);
			} else if (config.initialPlaybackOrPublishParams._type === 'UiMediaSoupPublishingParameters') {
				this.publish(config.initialPlaybackOrPublishParams);
			}
		}
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	private publishPostponer = new Postponer();

	async publish(parameters: UiMediaSoupPublishingParametersConfig) {
		return this.publishPostponer.postponeUntil(
			async () => {
				if (this.conference != null) {
					this.stop();
				}
				this.connectionStatus.status = "publish";
				this.updateStateCssClass();
				this.$video.muted = true;

				try {
					let {sourceStreams, targetStream} = await retrieveUserMedia(parameters.audioConstraints, parameters.videoConstraints, parameters.screenSharingConstraints,
						(endedMediaStream, isDisplay) => {
							if (this.currentSourceStreams.indexOf(endedMediaStream) != -1) {
								console.log(`${isDisplay ? "display" : "mic/cam"} stream ended`);
								this.onPublishedStreamEnded.fire({isDisplay: isDisplay});
								this.connectionStatus.status = "error";
								this.updateStateCssClass();
							}
						});
					this.currentSourceStreams = sourceStreams;
					this.$video.classList.toggle("mirrored", parameters.videoConstraints && !parameters.screenSharingConstraints);
					await this.publishMediaStream(targetStream, parameters);
					addVoiceActivityDetection(targetStream, () => this.onVoiceActivityChanged.fire({active: true}), () => this.onVoiceActivityChanged.fire({active: false}));
				} catch (e) {
					console.error(e);
					this.onPublishingFailed.fire({errorMessage: e.exception.toString(), reason: e.reason});
					this.stop();
					this.connectionStatus.status = "error";
					this.updateStateCssClass();
				}
			},
			() => this.connectionStatus.status !== "publish"
				|| (this.connectionStatus.connected && this.connectionStatus.audioStatus && this.connectionStatus.audioBitrate != 0)
		);
	}

	private async publishMediaStream(mediaStream: MediaStream, parameters: UiMediaSoupPublishingParametersConfig) {
		try {
			let conference = new Conference({
				uid: parameters.uid,
				token: parameters.token,
				params: {
					serverUrl: parameters.serverAddress,
					minBitrate: parameters.minBitrate,
					maxBitrate: parameters.maxBitrate,
					localVideo: this.$video,
					simulcast: true,
					errorAutoPlayCallback: (video: HTMLVideoElement, error: string) => {
						console.log("no autoplay on publisher?? " + error); // should never happen!
					},
					onStatusChange: (() => {
						let audioHasBeenActivelyStreaming = false;
						return (live: { audio: boolean, video: boolean }) => {
							console.log('onStatusChange', live);
							if (!audioHasBeenActivelyStreaming && live.audio) { // !! audio is enough
								audioHasBeenActivelyStreaming = true;
								this.onPublishingSucceeded.fire({});
							}
							this.connectionStatus.audioStatus = live.audio;
							this.updateStateCssClass();
							this.onPublishedStreamsStatusChanged.fire(live);
						}
					})(),
					onConnectionChange: connected => {
						this.connectionStatus.connected = connected;
						this.updateStateCssClass();
						this.onConnectionStateChanged.fire({connected});
					},
					onBitrate: (mediaType, bitrate) => {
						if (mediaType === "audio") {
							console.log("publisher audio bitrate: " + bitrate);
							this.connectionStatus.audioBitrate = bitrate;
							this.updateStateCssClass();
						}
					},
					onProfileChange: (profile: string) => {
						// not relevant for publisher
					}
				}
			});
			this.conference = conference;
			Conference.listenStreamEnded(mediaStream, () => {
				console.log("targetStream ended. stopping.")
				this.stop();
			});
			await this.conference.publish(mediaStream);
		} catch (e) {
			throw {
				exception: e,
				reason: WebRtcPublishingFailureReason.CONNECTION_ESTABLISHMENT_FAILED
			};
		}
	}


	private updateStateCssClass() {
		removeClassesByFunction(this.$main.classList, className => className.startsWith("state-"));
		if (this.connectionStatus.status === "idle") {
			this.$main.classList.add("state-idle");
		} else if (this.connectionStatus.status === "publish") {
			if (!this.connectionStatus.connected || !this.connectionStatus.audioStatus || this.connectionStatus.audioBitrate == 0) {
				this.$main.classList.add("state-connecting");
			} else {
				this.$main.classList.add("state-streaming");
			}
		} else if (this.connectionStatus.status === "playback") {
			if (!this.connectionStatus.connected || this.connectionStatus.audioBitrate == 0) {
				this.$main.classList.add("state-connecting");
			} else {
				this.$main.classList.add("state-streaming");
			}
		} else if (this.connectionStatus.status === "error") {
			this.$main.classList.add("state-error");
		}
	}

	playback(parameters: UiMediaSoupPlaybackParametersConfig): void {
		console.log(parameters);
		if (this.conference != null) {
			this.stop();
		}
		this.connectionStatus.status = "playback";
		this.updateStateCssClass();
		this.$video.muted = false;

		this.conference = new Conference({
			uid: parameters.uid,
			token: null,
			params: {
				serverUrl: parameters.serverAddress,
				audio: parameters.audio,
				video: parameters.video,
				minBitrate: 100,
				maxBitrate: 10000000,
				localVideo: this.$video,
				simulcast: true,
				errorAutoPlayCallback: () => {
					console.error("no autoplay");
				},
				onConnectionChange: connected => {
					this.connectionStatus.connected = connected;
					this.updateStateCssClass();
					this.onConnectionStateChanged.fire({connected});
				},
				onProfileChange: (profile: string) => {
					console.log("profile" + profile);
					this.$profileDisplay.innerText = profile;
					this.onPlaybackProfileChanged.fire({profile: UiMulticastPlaybackProfile[profile.toUpperCase() as any] as any});
				},
				onBitrate: (mediaType, bitrate) => {
					if (mediaType === "audio") {
						console.log("subscriber audio bitrate: " + bitrate);
						this.connectionStatus.audioBitrate = bitrate;
						this.updateStateCssClass();
					}
				}
			},
		});
		this.conference.play()
			.then(() => {
				this.onPlaybackSucceeded.fire({});
			})
			.catch(() => {
				this.onPlaybackFailed.fire({});
				this.connectionStatus.status = "error";
				this.updateStateCssClass();
			});
		;

		this.$video.classList.remove("mirrored");
	}

	stop() {
		this.currentSourceStreams = [];
		if (this.conference != null) {
			this.conference.stop();
			this.$video.classList.remove("mirrored");
		}
		this.updateStateCssClass();
		this.$video.srcObject = null; // make sure this happens even if the conference library fails doing it!

		this.connectionStatus.status = "idle";
		this.connectionStatus.audioStatus = false;
		this.connectionStatus.audioBitrate = -1;
	}

	update(config: UiMediaSoupV2WebRtcClientConfig): void {
		const iconsChanged = !arraysEqual(config.icons, this._config.icons);

		this._config = config;

		this.$main.classList.toggle("activity-line-visible", config.activityLineVisible);
		this.$main.style.setProperty("--activity-line-inactive-color", createUiColorCssString(config.activityInactiveColor));
		this.$main.style.setProperty("--activity-line-inactive-color", createUiColorCssString(config.activityActiveColor));

		if (iconsChanged) {
			this.$icons.innerHTML = '';
			config.icons.forEach(iconUrl => {
				const $img = document.createElement("img");
				$img.classList.add("icon");
				$img.src = iconUrl;
				this.$icons.appendChild($img);
			});
		}

		this.$caption.classList.toggle("hidden", config.caption == null);
		this.$caption.innerText = config.caption;

		if (this.$image.src !== config.noVideoImageUrl) { // only do this if actually changed, since the image looses its naturalWidth/naturalHeight for a short (but important!) time!
			this.$image.src = config.noVideoImageUrl;
		}

		this.$video.volume = config.playbackVolume;

		this.updateVideoVisibility();

		this.onResize();
	}

	private updateVideoVisibility() {
		if (this.isVideoShown() || this._config.noVideoImageUrl == null) {
			this.$image.classList.add('hidden');
			this.$video.classList.remove('hidden');
		} else {
			this.$image.classList.remove('hidden');
			this.$video.classList.add('hidden');
		}
	}

	setActive(active: boolean): void {
		this.$main.classList.toggle("active", active);
	}

	private isVideoShown() {
		return !!(this.$video.currentTime > 0 && !this.$video.paused && !this.$video.ended && this.$video.readyState > 2 && (this.$video.srcObject as MediaStream).getVideoTracks().length > 0);
	}

	onResize(): void {
		// this component consists of a video display and a caption. unfortunately, this makes sizing impossible to be calculated by CSS
		let availableHeight = this.getHeight() - this.$caption.offsetHeight;
		if (availableHeight <= 0) {
			this.$video.style.width = "0";
			this.$video.style.height = "0";
			return;
		}

		let displayAreaAspectRatio: number;
		if (this._config.displayAreaAspectRatio != null) {
			displayAreaAspectRatio = this._config.displayAreaAspectRatio;
		} else if (this.isVideoShown() && this.$video.videoWidth > 0) {
			displayAreaAspectRatio = this.$video.videoWidth / this.$video.videoHeight;
		} else if (this.$image.naturalWidth) {
			displayAreaAspectRatio = this.$image.naturalWidth / this.$image.naturalHeight;
		} else {
			displayAreaAspectRatio = 4 / 3;
		}

		this.$videoContainer.style.removeProperty("width");
		let videoSize = calculateDisplayModeInnerSize(
			{width: this.getWidth(), height: availableHeight},
			{width: 100, height: 100 / displayAreaAspectRatio},
			UiPageDisplayMode.FIT_SIZE,
			1,
			false
		);
		this.$image.style.width = videoSize.width + "px";
		this.$image.style.height = videoSize.height + "px";
		this.$video.style.width = videoSize.width + "px";
		this.$video.style.height = videoSize.height + "px";

		let spinnerSize = Math.min(this.getWidth(), this.getHeight()) / 4;
		this.$spinner.style.width = spinnerSize + "px";
		this.$spinner.style.height = spinnerSize + "px";
	}

	setContextMenuContent(requestId: number, component: UiComponent): void {
		this.contextMenu.setContent(component, requestId);
	}

	closeContextMenu(requestId: number): void {
		this.contextMenu.close(requestId);
	}

	public static async enumerateDevices(): Promise<UiMediaDeviceInfoConfig[]> {
		return enumerateDevices();
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiMediaSoupV2WebRtcClient", UiMediaSoupV2WebRtcClient);

