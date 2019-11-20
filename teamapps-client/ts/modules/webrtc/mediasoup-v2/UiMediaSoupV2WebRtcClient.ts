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

import {AbstractUiComponent} from "../../AbstractUiComponent";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {arraysEqual, calculateDisplayModeInnerSize, parseHtml, removeClassesByFunction} from "../../Common";
import {
	UiMediaSoupV2WebRtcClient_ClickedEvent,
	UiMediaSoupV2WebRtcClient_ConnectionStateChangedEvent,
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
import {UiMediaSoupPlaybackParamatersConfig} from "../../../generated/UiMediaSoupPlaybackParamatersConfig";
import {UiMediaSoupPublishingParametersConfig} from "../../../generated/UiMediaSoupPublishingParametersConfig";
import {UiVideoTrackConstraintsConfig} from "../../../generated/UiVideoTrackConstraintsConfig";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {UiMulticastPlaybackProfile} from "../../../generated/UiMulticastPlaybackProfile";
import {executeWhenFirstDisplayed} from "../../util/ExecuteWhenFirstDisplayed";
import {createUiColorCssString} from "../../util/CssFormatUtil";
import vad, {VoiceActivityDetectionHandle} from "voice-activity-detection";
import {UiPageDisplayMode} from "../../../generated/UiPageDisplayMode";
import {MultiStreamsMixer} from "../../util/MultiStreamsMixer";
import {UiScreenSharingConstraintsConfig} from "../../../generated/UiScreenSharingConstraintsConfig";
import {MediaDevicesExtended} from "./lib/interfaces";
import {MediaStreamWithMixiSizingInfo, MixSizingInfo} from "./lib/MultiStreamsMixer";
import {UiAudioTrackConstraintsConfig} from "../../../generated/UiAudioTrackConstraintsConfig";
import {WebRtcPublishingFailureReason} from "../../../generated/WebRtcPublishingFailureReason";

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

	private $main: HTMLDivElement;
	private conference: Conference;
	private $image: HTMLImageElement;
	private $videoContainer: HTMLElement;
	private $video: HTMLVideoElement;
	private $profileDisplay: HTMLElement;
	private $icons: HTMLImageElement;
	private $caption: HTMLElement;
	private multiStreamMixer: MultiStreamsMixer;

	private $spinner: HTMLElement;
	private currentSourceStreams: MediaStream[];

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

		[this.$main, this.$caption].forEach($element => $element.addEventListener("click", () => {
			console.log("click");
			this.onClicked.fire({})
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
			console.log("progress");
			this.updateVideoVisibility();
			this.onResize()
		});
		this.$video.addEventListener("timeupdate", ev => {
			console.log("timeupdate");
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

	@executeWhenFirstDisplayed(true)
	async publish(parameters: UiMediaSoupPublishingParametersConfig) {
		if (this.conference != null) {
			this.stop();
		}
		this.setStateCssClass("connecting");
		this.$video.muted = true;

		try {
			let {sourceStreams, targetStream} = await this.retrieveUserMedia(parameters.audioConstraints, parameters.videoConstraints, parameters.screenSharingConstraints);
			this.currentSourceStreams = sourceStreams;
			this.$video.classList.add("mirrored");
			await this.publishMediaStream(targetStream, parameters);
			this.addVoiceActivityDetection(targetStream);
		} catch (e) {
			console.error(e);
			this.onPublishingFailed.fire({errorMessage: e.exception.toString(), reason: e.reason});
			this.stop();
			this.setStateCssClass("error");
		}
	}

	private addVoiceActivityDetection(mediaStream: MediaStream) {
		if ((window as any).AudioContext && mediaStream.getAudioTracks().length > 0) {
			console.log("AudioContext detected");
			let audioContext = new AudioContext();
			console.log("AudioContext created");
			let vadHandle = vad(audioContext, mediaStream, {
				onVoiceStart: () => {
					this.onVoiceActivityChanged.fire({active: true});
				},
				onVoiceStop: () => {
					this.onVoiceActivityChanged.fire({active: false});
				}
			});
			console.log("vad attached");
			Conference.listenStreamEnded(mediaStream, () => {
				vadHandle.destroy();
			});
		}
	}

	private async retrieveUserMedia(audioConstraints: UiAudioTrackConstraintsConfig, videoConstraints: UiVideoTrackConstraintsConfig, screenSharingConstraints: UiScreenSharingConstraintsConfig) {
		let micCamStream: MediaStream = null;
		try {
			if (audioConstraints != null || videoConstraints != null) {
				micCamStream = await window.navigator.mediaDevices.getUserMedia({audio: audioConstraints, video: UiMediaSoupV2WebRtcClient.createVideoConstraints(videoConstraints)}); // rejected if user denies!
				Conference.listenStreamEnded(micCamStream, () => {
					if (this.currentSourceStreams.indexOf(micCamStream) != -1) {
						console.log('camera stream ended');
						this.onPublishedStreamEnded.fire({isDisplay: false});
						this.setStateCssClass("error");
					}
				});
			}
		} catch (e) {
			throw {
				exception: e,
				reason: WebRtcPublishingFailureReason.CAM_MIC_MEDIA_RETRIEVAL_FAILED
			};
		}

		let displayStream: MediaStream = null;
		try {
			if (screenSharingConstraints != null) {
				displayStream = await this.getDisplayStream(screenSharingConstraints); // rejected if user denies!
				Conference.listenStreamEnded(displayStream, () => {
					if (this.currentSourceStreams.indexOf(displayStream) != -1) {
						console.log('display stream ended');
						this.onPublishedStreamEnded.fire({isDisplay: true});
						this.setStateCssClass("error");
					}
				});
			}
		} catch (e) {
			throw {
				exception: e,
				reason: WebRtcPublishingFailureReason.DISPLAY_MEDIA_RETRIEVAL_FAILED
			};
		}

		let targetStream: MediaStream;
		if (displayStream != null && micCamStream != null) {
			try {
				let streamsWithMixSizingInfo: MediaStreamWithMixiSizingInfo[] = [];
				streamsWithMixSizingInfo.push({
					mediaStream: displayStream,
					mixSizingInfo: {fullcanvas: true}
				});

				let cameraMixSizingInfo: MixSizingInfo;
				if (micCamStream.getVideoTracks().length > 0) {
					const displayStreamDimensions = await Conference.determineVideoSize(displayStream);
					const mainStreamShortDimension = Math.min(displayStreamDimensions.width, displayStreamDimensions.height);
					const cameraAspectRatio = micCamStream.getTracks().filter(t => t.kind === "video")[0].getSettings().aspectRatio || 4 / 3;
					const pictureInPictureHeight = Math.round((25 / 100) * mainStreamShortDimension);
					const pictureInPictureWidth = Math.round(pictureInPictureHeight * cameraAspectRatio);
					cameraMixSizingInfo = {
						width: pictureInPictureWidth,
						height: pictureInPictureHeight,
						right: 0,
						top: 0
					};
				} else {
					cameraMixSizingInfo = {}; // no camera track. audio only. no mix sizing info needed
				}
				streamsWithMixSizingInfo.push({mediaStream: micCamStream, mixSizingInfo: cameraMixSizingInfo});
				targetStream = await Conference.mixStreams(streamsWithMixSizingInfo, UiMediaSoupV2WebRtcClient.createDisplayMediaStreamConstraints(screenSharingConstraints), 10);
			} catch (e) {
				throw {
					exception: e,
					reason: WebRtcPublishingFailureReason.VIDEO_MIXING_FAILED
				};
			}
		} else {
			targetStream = micCamStream || displayStream;
		}

		let sourceStreams: MediaStream[] = [];
		if (micCamStream != null) {
			sourceStreams.push(micCamStream);
		}
		if (displayStream != null) {
			sourceStreams.push(displayStream);
		}
		return {sourceStreams, targetStream};
	}

	private async publishMediaStream(mediaStream: MediaStream, parameters: UiMediaSoupPublishingParametersConfig) {
		try {
			this.conference = new Conference({
				uid: parameters.uid,
				token: parameters.token,
				params: {
					serverUrl: `https://${parameters.serverAdress}:${parameters.serverPort}`,
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
							console.log('onStatusChange', live)
							if (!audioHasBeenActivelyStreaming && live.audio) { // !! audio is enough
								audioHasBeenActivelyStreaming = true;
								this.onPublishingSucceeded.fire({});
								this.setStateCssClass("streaming");
							}
							if (live.audio == false) {
								this.setStateCssClass("connecting");
							}
							this.onPublishedStreamsStatusChanged.fire(live);
						}
					})(),
					onConnectionChange: connected => {
						if (connected) {
							this.setStateCssClass("streaming");
						} else {
							this.setStateCssClass("connecting");
						}
						this.onConnectionStateChanged.fire({connected});
					},
					onProfileChange: (profile: string) => {
						// not relevant for publisher
					}
				}
			});
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

	private async getDisplayStream(screenSharingConstraints: UiScreenSharingConstraintsConfig) {
		if (UiMediaSoupV2WebRtcClient.canPublishScreen()) {
			return await (window.navigator.mediaDevices as MediaDevicesExtended).getDisplayMedia(UiMediaSoupV2WebRtcClient.createDisplayMediaStreamConstraints(screenSharingConstraints));
		} else {
			throw new Error("Cannot share screen! Browser does not provide the corresponding API!");
		}
	}

	private setStateCssClass(state: "idle" | "connecting" | "streaming" | "error") {
		removeClassesByFunction(this.$main.classList, className => className.startsWith("state-"));
		this.$main.classList.add("state-" + state);
	}

	@executeWhenFirstDisplayed(true)
	playback(parameters: UiMediaSoupPlaybackParamatersConfig): void {
		console.log(parameters);
		if (this.conference != null) {
			this.stop();
		}
		this.setStateCssClass("connecting");
		this.$video.muted = false;

		this.conference = new Conference({
			uid: parameters.uid,
			token: null,
			params: {
				serverUrl: `https://${parameters.serverAdress}:${parameters.serverPort}`,
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
					if (connected) {
						this.setStateCssClass("streaming");
					} else {
						this.setStateCssClass("connecting");
					}
					this.onConnectionStateChanged.fire({connected});
				},
				onProfileChange: (profile: string) => {
					console.log("profile" + profile);
					this.$profileDisplay.innerText = profile;
					this.onPlaybackProfileChanged.fire({profile: UiMulticastPlaybackProfile[profile.toUpperCase() as any] as any});
				},
				onBitrate: (mediaType, bitrate) => {
					if (mediaType === "audio" && bitrate === 0) {
						this.setStateCssClass("connecting");
					} else {
						this.setStateCssClass("streaming");
					}
				}
			},
		});
		this.conference.play()
			.then(() => {
				this.onPlaybackSucceeded.fire({});
				this.setStateCssClass("streaming");
			})
			.catch(() => {
				this.onPlaybackFailed.fire({});
				this.setStateCssClass("error");
			});
		;

		this.$video.classList.remove("mirrored");
	}

	@executeWhenFirstDisplayed()
	stop() {
		this.currentSourceStreams = [];
		if (this.conference != null) {
			this.conference.stop();
			this.$video.classList.remove("mirrored");
		}
		if (this.multiStreamMixer != null) {
			this.multiStreamMixer.close();
		}
		this.setStateCssClass("idle");
		this.$video.srcObject = null; // make sure this happens even if the conference library fails doing it!
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

	private static createVideoConstraints(videoConstraints: UiVideoTrackConstraintsConfig): MediaTrackConstraints {
		return videoConstraints && {
			...videoConstraints,
			facingMode: null // TODO UiVideoFacingMode[videoConstraints.facingMode].toLocaleLowerCase() ==> make nullable!!!!
		};
	}

	private static createDisplayMediaStreamConstraints(screenSharingConstraints: UiScreenSharingConstraintsConfig) {
		return {
			video: screenSharingConstraints && {
				frameRate: {max: 5, ideal: 5},
				width: {max: screenSharingConstraints.maxWidth, ideal: screenSharingConstraints.maxWidth},
				height: {max: screenSharingConstraints.maxHeight, ideal: screenSharingConstraints.maxHeight}
			},
			audio: false, // TODO this might be interesting for sharing the actual computer audio...
		};
	}

	public static canPublishScreen() {
		return (window.navigator.mediaDevices as any).getDisplayMedia != null;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiMediaSoupV2WebRtcClient", UiMediaSoupV2WebRtcClient);

