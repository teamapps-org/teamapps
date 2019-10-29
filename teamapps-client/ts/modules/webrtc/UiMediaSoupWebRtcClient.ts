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

import {AbstractUiComponent} from "../AbstractUiComponent";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {calculateDisplayModeInnerSize, parseHtml} from "../Common";
import {
	UiMediaSoupWebRtcClient_ActivityChangedEvent, UiMediaSoupWebRtcClient_ClickedEvent,
	UiMediaSoupWebRtcClient_PlaybackProfileChangedEvent,
	UiMediaSoupWebRtcClientCommandHandler,
	UiMediaSoupWebRtcClientConfig,
	UiMediaSoupWebRtcClientEventSource
} from "../../generated/UiMediaSoupWebRtcClientConfig";
import {Conference} from "./conference";
import {UiMediaSoupPlaybackParamatersConfig} from "../../generated/UiMediaSoupPlaybackParamatersConfig";
import {UiMediaSoupPublishingParametersConfig} from "../../generated/UiMediaSoupPublishingParametersConfig";
import {UiVideoTrackConstraintsConfig} from "../../generated/UiVideoTrackConstraintsConfig";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiMulticastPlaybackProfile} from "../../generated/UiMulticastPlaybackProfile";
import {executeWhenFirstDisplayed} from "../util/ExecuteWhenFirstDisplayed";
import {createUiColorCssString} from "../util/CssFormatUtil";
import vad, {VoiceActivityDetectionHandle} from "voice-activity-detection";
import {UiPageDisplayMode} from "../../generated/UiPageDisplayMode";
import {UiAudioTrackConstraintsConfig} from "../../generated/UiAudioTrackConstraintsConfig";
import {checkChromeExtensionAvailable, getScreenConstraints, isChrome} from "../util/ScreenCapturing";
import {UiWebRtcPublishingErrorReason} from "../../generated/UiWebRtcPublishingErrorReason";
import {determineVideoSize, MixSizingInfo, MultiStreamsMixer} from "../util/MultiStreamsMixer";
import {UiScreenSharingConstraintsConfig} from "../../generated/UiScreenSharingConstraintsConfig";

export class UiMediaSoupWebRtcClient extends AbstractUiComponent<UiMediaSoupWebRtcClientConfig> implements UiMediaSoupWebRtcClientCommandHandler, UiMediaSoupWebRtcClientEventSource {
	public readonly onPlaybackProfileChanged: TeamAppsEvent<UiMediaSoupWebRtcClient_PlaybackProfileChangedEvent> = new TeamAppsEvent(this);
	public readonly onActivityChanged: TeamAppsEvent<UiMediaSoupWebRtcClient_ActivityChangedEvent> = new TeamAppsEvent(this);
	public readonly onClicked: TeamAppsEvent<UiMediaSoupWebRtcClient_ClickedEvent> = new TeamAppsEvent(this);

	private $main: HTMLDivElement;
	private conference: Conference;
	private $video: HTMLMediaElement;
	private $profileDisplay: HTMLElement;
	private $icon: HTMLImageElement;
	private $caption: HTMLElement;
	private voiceActivityDetectionHandle: VoiceActivityDetectionHandle;
	private multiStreamMixer: MultiStreamsMixer;

	constructor(config: UiMediaSoupWebRtcClientConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiMediaSoupWebRtcClient">
	<div class="video-container">
		<video class="video" playsinline></video>
		<img class="icon"></img>
	</div>
	<div class="caption"></div>
	<div class="profile hidden">.</div>
</div>`);
		this.$video = this.$main.querySelector<HTMLMediaElement>(":scope video");
		this.$profileDisplay = this.$main.querySelector(":scope .profile");
		this.$icon = this.$main.querySelector(":scope .icon");
		this.$caption = this.$main.querySelector(":scope .caption");

		this.$main.addEventListener("click", () => this.onClicked.fire({}));
		this.$video.addEventListener("play", ev => this.update(this._config));
		this.$video.addEventListener("playing", ev => this.update(this._config));
		this.$video.addEventListener("stalled", ev => this.update(this._config));
		this.$video.addEventListener("waiting", ev => this.update(this._config));
		this.$video.addEventListener("ended", ev => this.update(this._config));
		this.$video.addEventListener("suspend", ev => this.update(this._config));
		this.$video.addEventListener("abort", ev => this.update(this._config));

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

		this.conference = new Conference({
			uid: parameters.uid,
			token: parameters.token,
			params: {
				serverUrl: parameters.serverUrl,
				minBitrate: parameters.minBitrate,
				maxBitrate: parameters.maxBitrate,
				getUserMedia: async () => {
					this.voiceActivityDetectionHandle && this.voiceActivityDetectionHandle.destroy();
					this.multiStreamMixer = await this.getUserMedia(parameters.audioConstraints, parameters.videoConstraints, parameters.screenSharingConstraints);

					let mixedStream = await this.multiStreamMixer.getMixedStream();

					if (mixedStream.getAudioTracks().length > 0) {
						this.voiceActivityDetectionHandle = vad(new AudioContext(), mixedStream, {
							onVoiceStart: () => {
								this.onActivityChanged.fire({active: true});
							},
							onVoiceStop: () => {
								this.onActivityChanged.fire({active: false});
							}
						});
					}

					return mixedStream;
				},
				localVideo: this.$video,
				errorAutoPlayCallback: () => {
					console.error("no autoplay on publisher??");
				},
				onProfileChange: (profile: string) => {
					console.error("profile changed on publisher?? " + profile);
				}
			}
		});
		this.conference.publish();
	}

	private async getUserMedia(audioConstraints?: UiAudioTrackConstraintsConfig, videoConstraints?: UiVideoTrackConstraintsConfig, screenSharingConstraints?: UiScreenSharingConstraintsConfig) {
		let camMicStream: MediaStream;
		let screenStream: MediaStream;

		if (audioConstraints || videoConstraints) {
			let gumConstraints: MediaStreamConstraints = {
				audio: audioConstraints,
				video: UiMediaSoupWebRtcClient.createVideoConstraints(videoConstraints)
			};
			console.log(gumConstraints);
			camMicStream = await window.navigator.mediaDevices.getUserMedia(gumConstraints);
		}

		if (screenSharingConstraints != null) {
			const canProbablyPublishScreen = !isChrome || await checkChromeExtensionAvailable();
			if (canProbablyPublishScreen) {
				const screenConstraints = await getScreenConstraints({maxWidth: screenSharingConstraints.maxWidth, maxHeight: screenSharingConstraints.maxHeight});
				try {
					screenStream = await navigator.mediaDevices.getUserMedia({video: screenConstraints});
				} catch (e) {
					console.error("CANNOT_GET_SCREEN_MEDIA_STREAM");
					throw UiWebRtcPublishingErrorReason.CANNOT_GET_SCREEN_MEDIA_STREAM;
				}
			} else {
				console.error("CHROME_SCREEN_SHARING_EXTENSION_NOT_INSTALLED");
				throw UiWebRtcPublishingErrorReason.CHROME_SCREEN_SHARING_EXTENSION_NOT_INSTALLED;
			}
		}

		if (camMicStream != null && screenStream != null) {
			const screenStreamDimensions = await determineVideoSize(screenStream);

			let camMicStreamSizingInfo: MixSizingInfo = {};
			if (camMicStream.getVideoTracks().length > 0) {
				const screenStreamShortDimension = Math.min(screenStreamDimensions.width, screenStreamDimensions.height);
				const cameraAspectRatio = camMicStream.getTracks().filter(t => t.kind === "video")[0].getSettings().aspectRatio;
				const pictureInPictureHeight = Math.round((25 / 100) * screenStreamShortDimension);
				const pictureInPictureWidth = Math.round(pictureInPictureHeight * cameraAspectRatio);

				camMicStreamSizingInfo = {
					width: pictureInPictureWidth,
					height: pictureInPictureHeight,
					left: screenStreamDimensions.width - pictureInPictureWidth,
					top: 0
				};
			}
			return new MultiStreamsMixer([
					{
						mediaStream: screenStream,
						mixSizingInfo: {
							...screenStreamDimensions, fullcanvas: true
						}
					},
					{mediaStream: camMicStream, mixSizingInfo: camMicStreamSizingInfo}
				],
				(videoConstraints && videoConstraints.frameRate) || 10
			);
		} else if (camMicStream != null) {
			return new MultiStreamsMixer([{mediaStream: camMicStream, mixSizingInfo: {}}]);
		} else if (screenStream != null) {
			return new MultiStreamsMixer([{mediaStream: screenStream, mixSizingInfo: {}}]);
		}
	}

	@executeWhenFirstDisplayed(true)
	playback(parameters: UiMediaSoupPlaybackParamatersConfig): void {
		console.log(parameters);
		if (this.conference != null) {
			this.stop();
		}

		this.conference = new Conference({
			uid: parameters.uid,
			token: null,
			params: {
				serverUrl: parameters.serverUrl,
				audio: parameters.audio,
				video: parameters.video,
				minBitrate: 100,
				maxBitrate: 10000000,
				getUserMedia: null,
				localVideo: this.$video,
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
		this.conference.play();

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
	}

	update(config: UiMediaSoupWebRtcClientConfig): void {
		this._config = config;

		this.$main.classList.toggle("activity-line-visible", config.activityLineVisible);
		this.$main.style.setProperty("--activity-line-inactive-color", createUiColorCssString(config.activityInactiveColor));
		this.$main.style.setProperty("--activity-line-inactive-color", createUiColorCssString(config.activityActiveColor));

		this.$icon.classList.toggle("hidden", config.icon == null);
		this.$icon.src = config.icon != null ? this._context.getIconPath(config.icon, 64) : "#";

		this.$caption.classList.toggle("hidden", config.caption == null);
		this.$caption.innerText = config.caption;

		if (this.isVideoShown() || config.noVideoImageUrl == null) {
			this.$video.style.backgroundImage = `none`;
		} else {
			this.$video.style.backgroundImage = `url(${config.noVideoImageUrl})`;
		}

		this.onResize();
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

		if (this._config.displayAreaAspectRatio != null) {
			let videoSize = calculateDisplayModeInnerSize(
				{width: this.getWidth(), height: availableHeight},
				{width: 100, height: 100 / this._config.displayAreaAspectRatio},
				UiPageDisplayMode.FIT_SIZE,
				1,
				false
			);
			this.$video.style.width = videoSize.width + "px";
			this.$video.style.height = videoSize.height + "px";
		} else {
			this.$video.style.width = "100%";
			this.$video.style.height = availableHeight + "px";
		}
	}

	private static createVideoConstraints(videoConstraints: UiVideoTrackConstraintsConfig): MediaTrackConstraints {
		return videoConstraints && {
			...videoConstraints,
			facingMode: null // TODO UiVideoFacingMode[videoConstraints.facingMode].toLocaleLowerCase() ==> make nullable!!!!
		};
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiMediaSoupWebRtcClient", UiMediaSoupWebRtcClient);

