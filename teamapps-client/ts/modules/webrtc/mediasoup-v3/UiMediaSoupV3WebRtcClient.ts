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
import {calculateDisplayModeInnerSize, parseHtml} from "../../Common";
import {UiMediaSoupPlaybackParamatersConfig} from "../../../generated/UiMediaSoupPlaybackParamatersConfig";
import {UiMediaSoupPublishingParametersConfig} from "../../../generated/UiMediaSoupPublishingParametersConfig";
import {UiVideoTrackConstraintsConfig} from "../../../generated/UiVideoTrackConstraintsConfig";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {executeWhenFirstDisplayed} from "../../util/ExecuteWhenFirstDisplayed";
import {createUiColorCssString} from "../../util/CssFormatUtil";
import vad, {VoiceActivityDetectionHandle} from "voice-activity-detection";
import {UiPageDisplayMode} from "../../../generated/UiPageDisplayMode";
import {UiAudioTrackConstraintsConfig} from "../../../generated/UiAudioTrackConstraintsConfig";
import {checkChromeExtensionAvailable, getScreenConstraints, isChrome} from "../../util/ScreenCapturing";
import {UiWebRtcPublishingErrorReason} from "../../../generated/UiWebRtcPublishingErrorReason";
import {determineVideoSize, MixSizingInfo, MultiStreamsMixer} from "../../util/MultiStreamsMixer";
import {UiScreenSharingConstraintsConfig} from "../../../generated/UiScreenSharingConstraintsConfig";
import {
	UiMediaSoupV3WebRtcClient_ClickedEvent,
	UiMediaSoupV3WebRtcClient_VoiceActivityChangedEvent,
	UiMediaSoupV3WebRtcClientCommandHandler,
	UiMediaSoupV3WebRtcClientConfig,
	UiMediaSoupV3WebRtcClientEventSource
} from "../../../generated/UiMediaSoupV3WebRtcClientConfig";
import {Sender} from "./sender";
import {Receiver} from "./receiver";

export class UiMediaSoupV3WebRtcClient extends AbstractUiComponent<UiMediaSoupV3WebRtcClientConfig> implements UiMediaSoupV3WebRtcClientCommandHandler, UiMediaSoupV3WebRtcClientEventSource {
	public readonly onVoiceActivityChanged: TeamAppsEvent<UiMediaSoupV3WebRtcClient_VoiceActivityChangedEvent> = new TeamAppsEvent(this);
	public readonly onClicked: TeamAppsEvent<UiMediaSoupV3WebRtcClient_ClickedEvent> = new TeamAppsEvent(this);

	private sender: Sender;
	private receiver: Receiver;

	private $main: HTMLDivElement;
	private $video: HTMLMediaElement;
	private $profileDisplay: HTMLElement;
	private $icon: HTMLImageElement;
	private $caption: HTMLElement;
	private voiceActivityDetectionHandle: VoiceActivityDetectionHandle;
	private multiStreamMixer: MultiStreamsMixer;

	constructor(config: UiMediaSoupV3WebRtcClientConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiMediaSoupV3WebRtcClient">
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

	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	@executeWhenFirstDisplayed(true)
	async publish(parameters: UiMediaSoupPublishingParametersConfig) {
		this.stop();

		this.sender = new Sender(parameters.serverAdress, parameters.serverPort);
		await this.sender.connect(eventType => {
			switch (eventType) {
				case "disconnect":
					break;
				case "newVideoProducer":
					break;
				case "videoProducerGone":
					break;
			}
		});
		this.sender.publish(parameters.uid, parameters.token, true, parameters.videoConstraints == null, (status, audioStream, videoStream) => {
			switch (status) {
				case "connecting":
					console.log("connecting for publishing");
					break;
				case "connected":
					console.log("connected for publishing");
					break;
				case "failed":
					console.error("failed publish");
					// TODO handle fail
					break;
				case "updateStream":
					console.log("updateStream");
					this.$video.srcObject = videoStream;
					break;
			}
		});
	}

	private async getUserMedia(audioConstraints?: UiAudioTrackConstraintsConfig, videoConstraints?: UiVideoTrackConstraintsConfig, screenSharingConstraints?: UiScreenSharingConstraintsConfig) {
		let camMicStream: MediaStream;
		let screenStream: MediaStream;

		if (audioConstraints || videoConstraints) {
			let gumConstraints: MediaStreamConstraints = {
				audio: audioConstraints,
				video: UiMediaSoupV3WebRtcClient.createVideoConstraints(videoConstraints)
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
		this.stop();
	}

	@executeWhenFirstDisplayed()
	stop() {
		if (this.sender != null) {
			this.sender.disconnect(() => {
			});
			this.sender = null;
			this.$video.classList.remove("mirrored");
		}
		if (this.receiver != null) {
			this.receiver.disconnect(() => {
			});
			this.receiver = null;
			this.$video.classList.remove("mirrored");
		}
		if (this.multiStreamMixer != null) {
			this.multiStreamMixer.close();
		}
	}

	update(config: UiMediaSoupV3WebRtcClientConfig): void {
		this._config = config;

		this.$main.classList.toggle("activity-line-visible", config.activityLineVisible);
		this.$main.style.setProperty("--activity-line-inactive-color", createUiColorCssString(config.activityInactiveColor));
		this.$main.style.setProperty("--activity-line-inactive-color", createUiColorCssString(config.activityActiveColor));

		this.$icon.classList.toggle("hidden", config.icon == null);
		this.$icon.src = config.icon != null ? config.icon : "#";

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

TeamAppsUiComponentRegistry.registerComponentClass("UiMediaSoupV3WebRtcClient", UiMediaSoupV3WebRtcClient);

