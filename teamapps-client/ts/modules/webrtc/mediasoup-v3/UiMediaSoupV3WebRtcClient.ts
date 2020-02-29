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
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {
	UiMediaSoupV3WebRtcClient_ClickedEvent,
	UiMediaSoupV3WebRtcClient_ConnectionStateChangedEvent,
	UiMediaSoupV3WebRtcClient_ContextMenuRequestedEvent,
	UiMediaSoupV3WebRtcClient_PlaybackFailedEvent,
	UiMediaSoupV3WebRtcClient_PlaybackProfileChangedEvent,
	UiMediaSoupV3WebRtcClient_PlaybackSucceededEvent,
	UiMediaSoupV3WebRtcClient_PublishedStreamEndedEvent,
	UiMediaSoupV3WebRtcClient_PublishedStreamsStatusChangedEvent,
	UiMediaSoupV3WebRtcClient_PublishingFailedEvent,
	UiMediaSoupV3WebRtcClient_PublishingSucceededEvent,
	UiMediaSoupV3WebRtcClient_VoiceActivityChangedEvent,
	UiMediaSoupV3WebRtcClientCommandHandler,
	UiMediaSoupV3WebRtcClientConfig,
	UiMediaSoupV3WebRtcClientEventSource
} from "../../../generated/UiMediaSoupV3WebRtcClientConfig";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {UiMediaSoupPublishingParametersConfig} from "../../../generated/UiMediaSoupPublishingParametersConfig";
import {arraysEqual, calculateDisplayModeInnerSize, parseHtml, removeClassesByFunction} from "../../Common";
import {ContextMenu} from "../../micro-components/ContextMenu";
import {MultiStreamsMixer} from "../../util/MultiStreamsMixer";
import {addVoiceActivityDetection, retrieveUserMedia} from "../MediaUtil";
import {createUiColorCssString} from "../../util/CssFormatUtil";
import {UiPageDisplayMode} from "../../../generated/UiPageDisplayMode";
import {UiComponent} from "../../UiComponent";
import {ConferenceApi} from "./lib/front/src/conference-api";
import {WebRtcPublishingFailureReason} from "../../../generated/WebRtcPublishingFailureReason";
import {Utils} from "./lib/front/src/utils";
import {UiMediaSoupPlaybackParametersConfig} from "../../../generated/UiMediaSoupPlaybackParametersConfig";

export class UiMediaSoupV3WebRtcClient extends AbstractUiComponent<UiMediaSoupV3WebRtcClientConfig> implements UiMediaSoupV3WebRtcClientCommandHandler, UiMediaSoupV3WebRtcClientEventSource {
	public readonly onPublishingSucceeded: TeamAppsEvent<UiMediaSoupV3WebRtcClient_PublishingSucceededEvent> = new TeamAppsEvent(this);
	public readonly onPublishingFailed: TeamAppsEvent<UiMediaSoupV3WebRtcClient_PublishingFailedEvent> = new TeamAppsEvent(this);
	public readonly onPublishedStreamEnded: TeamAppsEvent<UiMediaSoupV3WebRtcClient_PublishedStreamEndedEvent> = new TeamAppsEvent(this);
	public readonly onPublishedStreamsStatusChanged: TeamAppsEvent<UiMediaSoupV3WebRtcClient_PublishedStreamsStatusChangedEvent> = new TeamAppsEvent(this);
	public readonly onConnectionStateChanged: TeamAppsEvent<UiMediaSoupV3WebRtcClient_ConnectionStateChangedEvent> = new TeamAppsEvent(this);
	public readonly onPlaybackSucceeded: TeamAppsEvent<UiMediaSoupV3WebRtcClient_PlaybackSucceededEvent> = new TeamAppsEvent(this);
	public readonly onPlaybackFailed: TeamAppsEvent<UiMediaSoupV3WebRtcClient_PlaybackFailedEvent> = new TeamAppsEvent(this);
	public readonly onPlaybackProfileChanged: TeamAppsEvent<UiMediaSoupV3WebRtcClient_PlaybackProfileChangedEvent> = new TeamAppsEvent(this);
	public readonly onVoiceActivityChanged: TeamAppsEvent<UiMediaSoupV3WebRtcClient_VoiceActivityChangedEvent> = new TeamAppsEvent(this);
	public readonly onClicked: TeamAppsEvent<UiMediaSoupV3WebRtcClient_ClickedEvent> = new TeamAppsEvent(this);
	public readonly onContextMenuRequested: TeamAppsEvent<UiMediaSoupV3WebRtcClient_ContextMenuRequestedEvent> = new TeamAppsEvent(this);

	private $main: HTMLDivElement;
	private $image: HTMLImageElement;
	private $videoContainer: HTMLElement;
	private $video: HTMLVideoElement;
	private $profileDisplay: HTMLElement;
	private $icons: HTMLImageElement;
	private $caption: HTMLElement;
	private $spinner: HTMLElement;
	private contextMenu: ContextMenu;

	private multiStreamMixer: MultiStreamsMixer;
	private currentSourceStreams: MediaStream[];
	private conferenceApi: ConferenceApi;

	private connectionStatus: {
		status: "idle" | "publish" | "playback" | "error",
		// connected: boolean,
		// audioStatus: boolean,
		// audioBitrate: number // assume sending/receiving from the beginning
	} = {
		status: "idle",
		// connected: false,          // TODO
		// audioStatus: false,        // TODO
		// audioBitrate: -1           // TODO
	};


	constructor(config: UiMediaSoupV3WebRtcClientConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiMediaSoupV3WebRtcClient state-idle">
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
		this.$video.addEventListener("pause", ev => {
			console.log("pause");
			this.$video.play(); // happens when the video player gets detached under android while switching views
		});
		this.$video.addEventListener("progress", ev => {
			this.updateVideoVisibilities();
			this.onResize()
		});
		this.$video.addEventListener("timeupdate", ev => {
			this.updateVideoVisibilities();
			this.onResize()
		});
		["play", "playing", "stalled", "waiting", "ended", "suspend", "abort"].forEach(eventName => {
			this.$video.addEventListener(eventName, ev => {
				console.log(eventName);
				this.update(this._config)
			});
		});

		this.update(config);
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	async publish(parameters: UiMediaSoupPublishingParametersConfig) {
		if (this.conferenceApi != null) {
			this.stop();
		}
		this.connectionStatus.status = "publish";
		this.updateStateCssClass();
		this.$video.classList.toggle("mirrored", parameters.videoConstraints && !parameters.screenSharingConstraints);

		let targetStream: MediaStream;
		try {
			let streams = await retrieveUserMedia(parameters.audioConstraints, parameters.videoConstraints, parameters.screenSharingConstraints,
				(endedMediaStream, isDisplay) => {
					if (this.currentSourceStreams.indexOf(endedMediaStream) != -1) {
						console.log(`${isDisplay ? "display" : "mic/cam"} stream ended`);
						this.onPublishedStreamEnded.fire({isDisplay: isDisplay});
						this.connectionStatus.status = "error";
						this.updateStateCssClass();
					}
				});
			this.currentSourceStreams = streams.sourceStreams;
			targetStream = streams.targetStream;
		} catch (error) {
			console.error("Error during media retrieval", error);
			this.onPublishingFailed.fire({reason: error.reason, errorMessage: error.toString()});
			throw error;
		}

		try {
			this.conferenceApi = new ConferenceApi({
				stream: parameters.uid,
				token: parameters.token,
				url: parameters.serverAddress
			});
			await this.conferenceApi.publish(targetStream);
			addVoiceActivityDetection(targetStream, () => this.onVoiceActivityChanged.fire({active: true}), () => this.onVoiceActivityChanged.fire({active: false}));

			this.$video.muted = true;
			this.$video.srcObject = targetStream;
			try {
				await this.$video.play();
			} catch (error) {
				console.log('Could not playback local (publishing) video!', error);
			}

			this.onPublishingSucceeded.fire({}); // TODO move somewhere better once M2 is done?
		} catch (e) {
			console.error('Error while publishing!', e);
			if (e.statusCode) {
				console.error("HTTP status code: " + e.statusCode);
			}
			this.onPublishingFailed.fire({errorMessage: e.exception.toString(), reason: WebRtcPublishingFailureReason.CONNECTION_ESTABLISHMENT_FAILED});
			this.stop();
			this.connectionStatus.status = "error";
			this.updateStateCssClass();
		}
	}

	private updateStateCssClass() {
		removeClassesByFunction(this.$main.classList, className => className.startsWith("state-"));
		if (this.connectionStatus.status === "idle") {
			this.$main.classList.add("state-idle");
		} else if (this.connectionStatus.status === "publish") {
			// if (!this.connectionStatus.connected || !this.connectionStatus.audioStatus || this.connectionStatus.audioBitrate == 0) {
			// 	this.$main.classList.add("state-connecting");
			// } else {
			this.$main.classList.add("state-streaming");
			// }
		} else if (this.connectionStatus.status === "playback") {
			// if (!this.connectionStatus.connected || this.connectionStatus.audioBitrate == 0) {
			// 	this.$main.classList.add("state-connecting");
			// } else {
			this.$main.classList.add("state-streaming");
			// }
		} else if (this.connectionStatus.status === "error") {
			this.$main.classList.add("state-error");
		}
	}

	async playback(parameters: UiMediaSoupPlaybackParametersConfig) {
		console.log(parameters);
		if (this.conferenceApi != null) {
			this.stop();
		}
		this.connectionStatus.status = "playback";
		this.updateStateCssClass();

		const playVideoElement = async () => {
			this.$video.muted = false;
			try {
				await this.$video.play();
			} catch (error) {
				console.error('Unmuted autoplay failed!');
				this.onPlaybackFailed.fire({})
				this.$video.muted = true;
				this.$video.play().then(() => {
					console.log('autoplay successful, but only MUTED! TODO handle this gracefully!');
				});
			}
		};

		try {
			this.conferenceApi = new ConferenceApi({
				stream: parameters.uid,
				token: parameters.token,
				url: parameters.serverAddress
			});
			const mediaStream = await this.conferenceApi.subscribe();
			this.$video.srcObject = mediaStream;
			if (Utils.isSafari) {
				const onStreamChange = () => {
					this.$video.srcObject = new MediaStream(mediaStream.getTracks());
					playVideoElement();
				};
				mediaStream.addEventListener('addtrack', onStreamChange);
				mediaStream.addEventListener('removetrack', onStreamChange);
			} else if (Utils.isFirefox) {
				this.$video.addEventListener('pause', playVideoElement)
			}
			playVideoElement();
			this.onPlaybackSucceeded.fire({});
		} catch (error) {
			console.error("Error while starting playback!", error)
			this.onPlaybackFailed.fire({});
		}

		this.$video.classList.remove("mirrored");
	}

	stop() {
		this.currentSourceStreams = [];
		if (this.conferenceApi != null) {
			this.conferenceApi.close();
			this.conferenceApi.close(true);
			this.conferenceApi = null;
			this.$video.classList.remove("mirrored");
		}
		if (this.multiStreamMixer != null) {
			this.multiStreamMixer.close();
		}
		this.updateStateCssClass();
		this.$video.srcObject = null; // make sure this happens even if the conference library fails doing it!

		this.connectionStatus.status = "idle";
		// this.connectionStatus.audioStatus = false; // TODO
		// this.connectionStatus.audioBitrate = -1;   // TODO
	}

	update(config: UiMediaSoupV3WebRtcClientConfig): void {
		this.$main.classList.toggle("activity-line-visible", config.activityLineVisible);
		this.$main.style.setProperty("--activity-line-inactive-color", createUiColorCssString(config.activityInactiveColor));
		this.$main.style.setProperty("--activity-line-inactive-color", createUiColorCssString(config.activityActiveColor));

		if (!arraysEqual(config.icons, this._config.icons)) {
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

		if (this._config.playbackParameters != null && config.playbackParameters == null
			|| this._config.publishingParameters != null && config.publishingParameters == null) {
			this.stop();
		}
		if (this._config.playbackParameters == null && config.playbackParameters != null) { // TODO more sophisticated!!!
			this.playback(config.playbackParameters);
		} else if (this._config.publishingParameters == null && config.publishingParameters != null) { // TODO more sophisticated!!!
			this.publish(config.publishingParameters);
		}

		this._config = config;

		this.updateVideoVisibilities();
		this.onResize();
	}

	private updateVideoVisibilities() {
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

}

TeamAppsUiComponentRegistry.registerComponentClass("UiMediaSoupV3WebRtcClient", UiMediaSoupV3WebRtcClient);

