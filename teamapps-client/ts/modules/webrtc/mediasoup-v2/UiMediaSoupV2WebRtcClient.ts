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
import {calculateDisplayModeInnerSize, parseHtml, removeClassesByFunction} from "../../Common";
import {
	UiMediaSoupV2WebRtcClient_ClickedEvent,
	UiMediaSoupV2WebRtcClient_PlaybackFailedEvent,
	UiMediaSoupV2WebRtcClient_PlaybackProfileChangedEvent,
	UiMediaSoupV2WebRtcClient_PlaybackSucceededEvent,
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
import {MediaStreamConstraintsExtended} from "./lib/interfaces";

export class UiMediaSoupV2WebRtcClient extends AbstractUiComponent<UiMediaSoupV2WebRtcClientConfig> implements UiMediaSoupV2WebRtcClientCommandHandler, UiMediaSoupV2WebRtcClientEventSource {
	public readonly onPublishingSucceeded: TeamAppsEvent<UiMediaSoupV2WebRtcClient_PublishingSucceededEvent> = new TeamAppsEvent(this);
	public readonly onPublishingFailed: TeamAppsEvent<UiMediaSoupV2WebRtcClient_PublishingFailedEvent> = new TeamAppsEvent(this);
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
	private $icon: HTMLImageElement;
	private $caption: HTMLElement;
	private voiceActivityDetectionHandle: VoiceActivityDetectionHandle;
	private multiStreamMixer: MultiStreamsMixer;

	private $spinner: HTMLElement;

	constructor(config: UiMediaSoupV2WebRtcClientConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiMediaSoupV2WebRtcClient state-idle">
	<div class="video-container">
		<img class="image"></img>
		<video class="video" playsinline></video>
		<img class="icon"></img>
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
		this.$icon = this.$main.querySelector(":scope .icon");
		this.$caption = this.$main.querySelector(":scope .caption");
		this.$spinner = this.$main.querySelector(":scope .spinner");

		this.$main.addEventListener("click", () => {
			console.log("click");
			this.onClicked.fire({})
		});
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
	publish(parameters: UiMediaSoupPublishingParametersConfig): void {
		if (this.conference != null) {
			this.stop();
		}

		this.setStateCssClass("connecting");

		const camMicConstraints = {
			audio: parameters.audioConstraints,
			video: UiMediaSoupV2WebRtcClient.createVideoConstraints(parameters.videoConstraints)
		};
		this.conference = new Conference({
			uid: parameters.uid,
			token: parameters.token,
			params: {
				serverUrl: `https://${parameters.serverAdress}:${parameters.serverPort}`,
				minBitrate: parameters.minBitrate,
				maxBitrate: parameters.maxBitrate,
				constraints: parameters.screenSharingConstraints != null ? this.createScreenSharingConstraints(parameters, parameters.screenSharingConstraints) : camMicConstraints,
				additionalConstraints: parameters.screenSharingConstraints != null ? camMicConstraints : null,
				simulcast: true,
				localVideo: this.$video,
				// TODO other error callback?????
				errorAutoPlayCallback: () => {
					console.error("no autoplay on publisher??");
				},
				onProfileChange: (profile: string) => {
					console.error("profile changed on publisher?? " + profile);
				},
				mediaStreamCapturedCallback: mediaStream => {
					this.voiceActivityDetectionHandle && this.voiceActivityDetectionHandle.destroy();
					if (mediaStream.getAudioTracks().length > 0) {
						this.voiceActivityDetectionHandle = vad(new AudioContext(), mediaStream, {
							onVoiceStart: () => {
								this.onVoiceActivityChanged.fire({active: true});
							},
							onVoiceStop: () => {
								this.onVoiceActivityChanged.fire({active: false});
							}
						});
					}
					if (mediaStream.getVideoTracks().length > 0 && !parameters.screenSharingConstraints) {
						this.$video.classList.add("mirrored");
					}
				}
			}
		});
		this.conference.publish()
			.then(() => {
				this.onPublishingSucceeded.fire({});
				this.setStateCssClass("streaming");
			})
			.catch(() => {
				this.onPublishingFailed.fire({});
				this.setStateCssClass("error");
			});
	}

	private createScreenSharingConstraints(parameters: UiMediaSoupPublishingParametersConfig, screenSharingConstraints: UiScreenSharingConstraintsConfig): MediaStreamConstraintsExtended {
		return {
			video: screenSharingConstraints && {
				frameRate: {max: 5, ideal: 5},
				width: {max: screenSharingConstraints.maxWidth, ideal: screenSharingConstraints.maxWidth},
				height: {max: screenSharingConstraints.maxHeight, ideal: screenSharingConstraints.maxHeight}
			},
			isDisplay: true
		};
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
				constraints: null,
				simulcast: true,
				errorAutoPlayCallback: () => {
					console.error("no autoplay");
				},
				onProfileChange: (profile: string) => {
					console.log("profile" + profile);
					this.$profileDisplay.innerText = profile;
					this.onPlaybackProfileChanged.fire({profile: UiMulticastPlaybackProfile[profile.toUpperCase() as any] as any});
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
		if (this.conference != null) {
			this.conference.stop();
			this.$video.classList.remove("mirrored");
		}
		if (this.multiStreamMixer != null) {
			this.multiStreamMixer.close();
		}
		this.setStateCssClass("idle");
	}

	update(config: UiMediaSoupV2WebRtcClientConfig): void {
		this._config = config;

		this.$main.classList.toggle("activity-line-visible", config.activityLineVisible);
		this.$main.style.setProperty("--activity-line-inactive-color", createUiColorCssString(config.activityInactiveColor));
		this.$main.style.setProperty("--activity-line-inactive-color", createUiColorCssString(config.activityActiveColor));

		this.$icon.classList.toggle("hidden", config.icon == null);
		this.$icon.src = config.icon != null ? config.icon : "#";

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

}

TeamAppsUiComponentRegistry.registerComponentClass("UiMediaSoupV2WebRtcClient", UiMediaSoupV2WebRtcClient);

