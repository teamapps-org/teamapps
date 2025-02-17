/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
	UiMediaSoupV3WebRtcClient_SourceMediaTrackEndedEvent,
	UiMediaSoupV3WebRtcClient_SourceMediaTrackRetrievalFailedEvent,
	UiMediaSoupV3WebRtcClient_SubscribingFailedEvent,
	UiMediaSoupV3WebRtcClient_SubscribingSuccessfulEvent,
	UiMediaSoupV3WebRtcClient_SubscriptionPlaybackFailedEvent,
	UiMediaSoupV3WebRtcClient_TrackPublishingFailedEvent,
	UiMediaSoupV3WebRtcClient_TrackPublishingSuccessfulEvent,
	UiMediaSoupV3WebRtcClient_VoiceActivityChangedEvent,
	UiMediaSoupV3WebRtcClientCommandHandler,
	UiMediaSoupV3WebRtcClientConfig,
	UiMediaSoupV3WebRtcClientEventSource
} from "../../../generated/UiMediaSoupV3WebRtcClientConfig";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {UiMediaSoupPublishingParametersConfig} from "../../../generated/UiMediaSoupPublishingParametersConfig";
import {arraysEqual, calculateDisplayModeInnerSize, deepEquals, findClassesByFunction, parseHtml} from "../../Common";
import {ContextMenu} from "../../micro-components/ContextMenu";
import {addVoiceActivityDetection, createVideoConstraints, enumerateDevices, getDisplayStream} from "../MediaUtil";
import {UiPageDisplayMode} from "../../../generated/UiPageDisplayMode";
import {UiComponent} from "../../UiComponent";
import {UiMediaSoupPlaybackParametersConfig} from "../../../generated/UiMediaSoupPlaybackParametersConfig";
import {UiMediaDeviceInfoConfig} from "../../../generated/UiMediaDeviceInfoConfig";
import {MediaKind} from "mediasoup-client/lib/RtpParameters";
import {UiAudioTrackConstraintsConfig} from "../../../generated/UiAudioTrackConstraintsConfig";
import {UiVideoTrackConstraintsConfig} from "../../../generated/UiVideoTrackConstraintsConfig";
import {UiScreenSharingConstraintsConfig} from "../../../generated/UiScreenSharingConstraintsConfig";
import {MixSizingInfo, TrackWithMixSizingInfo, VideoTrackMixer} from "../VideoTrackMixer";
import {determineVideoSize} from "../MultiStreamsMixer";
import {UiMediaRetrievalFailureReason} from "../../../generated/UiMediaRetrievalFailureReason";
import {UiSourceMediaTrackType} from "../../../generated/UiSourceMediaTrackType";
import {ConferenceApi, Utils} from "./lib/avcore.client";
import {ConferenceInput} from "./lib/avcore";
import {GainController} from "../GainController";
import {UiToolButton} from "../../micro-components/UiToolButton";
import {VoiceActivityDetectionHandle} from "voice-activity-detection";

export class UiMediaSoupV3WebRtcClient extends AbstractUiComponent<UiMediaSoupV3WebRtcClientConfig> implements UiMediaSoupV3WebRtcClientCommandHandler, UiMediaSoupV3WebRtcClientEventSource {
	public readonly onSourceMediaTrackRetrievalFailed: TeamAppsEvent<UiMediaSoupV3WebRtcClient_SourceMediaTrackRetrievalFailedEvent> = new TeamAppsEvent();
	public readonly onSourceMediaTrackEnded: TeamAppsEvent<UiMediaSoupV3WebRtcClient_SourceMediaTrackEndedEvent> = new TeamAppsEvent();

	public readonly onTrackPublishingSuccessful: TeamAppsEvent<UiMediaSoupV3WebRtcClient_TrackPublishingSuccessfulEvent> = new TeamAppsEvent();
	public readonly onTrackPublishingFailed: TeamAppsEvent<UiMediaSoupV3WebRtcClient_TrackPublishingFailedEvent> = new TeamAppsEvent();

	public readonly onSubscribingSuccessful: TeamAppsEvent<UiMediaSoupV3WebRtcClient_SubscribingSuccessfulEvent> = new TeamAppsEvent();
	public readonly onSubscribingFailed: TeamAppsEvent<UiMediaSoupV3WebRtcClient_SubscribingFailedEvent> = new TeamAppsEvent();
	public readonly onSubscriptionPlaybackFailed: TeamAppsEvent<UiMediaSoupV3WebRtcClient_SubscriptionPlaybackFailedEvent> = new TeamAppsEvent();

	public readonly onConnectionStateChanged: TeamAppsEvent<UiMediaSoupV3WebRtcClient_ConnectionStateChangedEvent> = new TeamAppsEvent();

	public readonly onVoiceActivityChanged: TeamAppsEvent<UiMediaSoupV3WebRtcClient_VoiceActivityChangedEvent> = new TeamAppsEvent();
	public readonly onClicked: TeamAppsEvent<UiMediaSoupV3WebRtcClient_ClickedEvent> = new TeamAppsEvent();
	public readonly onContextMenuRequested: TeamAppsEvent<UiMediaSoupV3WebRtcClient_ContextMenuRequestedEvent> = new TeamAppsEvent();

	private $main: HTMLDivElement;
	private $image: HTMLImageElement;
	private $videoContainer: HTMLElement;
	private $video: HTMLVideoElement;
	private $profileDisplay: HTMLElement;
	private $bitrateDisplayWrapper: HTMLElement;
	private $audioBitrateDisplay: HTMLElement;
	private $videoBitrateDisplay: HTMLElement;
	private $toolsAndIconsBar: HTMLElement;
	private $toolButtons: HTMLElement;
	private $icons: HTMLImageElement;
	private $caption: HTMLElement;
	private $spinner: HTMLElement;
	private $unmuteButtonWrapper: HTMLElement;

	private contextMenu: ContextMenu;
	private conferenceClient: ConferenceApi;

	private gainController: GainController;
	private webcamTrack: MediaStreamTrack;
	private screenTrack: MediaStreamTrack;
	private videoTrackMixer: VideoTrackMixer;
	private targetStream: MediaStream;

	private connectionStatus: {
		rtcPeerConnectionState: RTCPeerConnectionState | null,
		shouldHaveAudio: boolean,
		shouldHaveVideo: boolean,
		audioBitrate: number,
		videoBitrate: number
	} = {
		rtcPeerConnectionState: null,
		shouldHaveAudio: false,
		shouldHaveVideo: false,
		audioBitrate: 0,
		videoBitrate: 0
	};
	private vad: VoiceActivityDetectionHandle;

	constructor(config: UiMediaSoupV3WebRtcClientConfig, context: TeamAppsUiContext) {
		super(config, context);

		// debug.enable(
		// 	'conference-api* mediasoup-client*' //enable conference api and mediasoup client logs
		// );


		this.$main = parseHtml(`<div class="UiMediaSoupV3WebRtcClient state-idle">
	<div class="video-container">
		<img class="image"></img>
		<video class="video" playsinline></video>
		<div class="tools-and-icons-bar">
		    <div class="tool-buttons"></div>
			<div class="icons"></div>
		</div>
		<div class="bitrate-display hidden">
			<div class="bitrate-audio"></div>
			<div class="bitrate-video"></div>
		</div>
		<div class="spinner-wrapper">
			<div class="spinner teamapps-spinner"></div>
		</div>
		<div class="unmute-button-wrapper hidden" style="z-index: 2; position: absolute; top: 0; left: 0">
			 <img class="unmute-button hidden" src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0id2hpdGUiIHdpZHRoPSIxMDAwMHB4IiBoZWlnaHQ9IjEwMDAwcHgiPjxwYXRoIGQ9Ik0xNi41IDEyYzAtMS43Ny0xLjAyLTMuMjktMi41LTQuMDN2Mi4yMWwyLjQ1IDIuNDVjLjAzLS4yLjA1LS40MS4wNS0uNjN6bTIuNSAwYzAgLjk0LS4yIDEuODItLjU0IDIuNjRsMS41MSAxLjUxQzIwLjYzIDE0LjkxIDIxIDEzLjUgMjEgMTJjMC00LjI4LTIuOTktNy44Ni03LTguNzd2Mi4wNmMyLjg5Ljg2IDUgMy41NCA1IDYuNzF6TTQuMjcgM0wzIDQuMjcgNy43MyA5SDN2Nmg0bDUgNXYtNi43M2w0LjI1IDQuMjVjLS42Ny41Mi0xLjQyLjkzLTIuMjUgMS4xOHYyLjA2YzEuMzgtLjMxIDIuNjMtLjk1IDMuNjktMS44MUwxOS43MyAyMSAyMSAxOS43M2wtOS05TDQuMjcgM3pNMTIgNEw5LjkxIDYuMDkgMTIgOC4xOFY0eiIvPjxwYXRoIGQ9Ik0wIDBoMjR2MjRIMHoiIGZpbGw9Im5vbmUiLz48L3N2Zz4="></img>
		</div>
	</div>
	<div class="caption"></div>
	<div class="profile hidden">.</div>
</div>`);
		this.$image = this.$main.querySelector(":scope .image");
		this.$videoContainer = this.$main.querySelector(":scope .video-container");
		this.$video = this.$main.querySelector<HTMLVideoElement>(":scope .video");
		this.$profileDisplay = this.$main.querySelector(":scope .profile");
		this.$bitrateDisplayWrapper = this.$main.querySelector(":scope .bitrate-display");
		this.$audioBitrateDisplay = this.$main.querySelector(":scope .bitrate-audio");
		this.$videoBitrateDisplay = this.$main.querySelector(":scope .bitrate-video");
		this.$toolsAndIconsBar = this.$main.querySelector(":scope .tools-and-icons-bar");
		this.$toolButtons = this.$main.querySelector(":scope .tool-buttons");
		this.$icons = this.$main.querySelector(":scope .icons");
		this.$caption = this.$main.querySelector(":scope .caption");
		this.$spinner = this.$main.querySelector(":scope .spinner");
		this.$unmuteButtonWrapper = this.$main.querySelector(":scope .unmute-button-wrapper");

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
			setTimeout(() => {
				this.$video.play(); // happens when the video player gets detached under android while switching views
			}, 1000);
		});
		["progress", "timeupdate"].forEach(eventName => {
			this.$video.addEventListener(eventName, ev => {
				this.updateVideoVisibility();
				this.onResize()
			});
		});
		["play"].forEach(eventName => {
			this.$video.addEventListener(eventName, ev => {
				console.log(eventName);
				// this.$unmuteButtonWrapper.classList.add("hidden");
			});
		});
		["stalled", "waiting", "ended", "suspend", "abort", "error", "emptied", "pause"].forEach(eventName => {
			this.$video.addEventListener(eventName, ev => {
				console.log(eventName);
			});
		});

		this._config = {
			toolButtons: config.toolButtons
		}; // make sure everything is regarded as new! _config will get set at the end again...

		this.gainController = new GainController(1);

		this.update(config);
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	private updateStateCssClasses() {
		const oldStateClasses = findClassesByFunction(this.$main.classList, className => className.startsWith("state-"));

		const newStateClasses: string[] = [];
		if (!this.connectionStatus.shouldHaveAudio && !this.connectionStatus.shouldHaveVideo) {
			newStateClasses.push("state-idle");
		} else if (this.connectionStatus.rtcPeerConnectionState !== "connected") {
			newStateClasses.push("state-connecting");
		} else if (this.connectionStatus.shouldHaveAudio && this.connectionStatus.audioBitrate === 0
			|| this.connectionStatus.shouldHaveVideo && this.connectionStatus.videoBitrate === 0) {
			newStateClasses.push("state-audio-loading");
		} else {
			newStateClasses.push("state-streaming")
		}

		oldStateClasses.filter(oldClass => newStateClasses.indexOf(oldClass) === -1).forEach(oldClass => this.$main.classList.remove(oldClass));
		newStateClasses.filter(newClass => oldStateClasses.indexOf(newClass) === -1).forEach(newClass => this.$main.classList.add(newClass));
	}

	private async stop() {
		console.log("stop()");
		if (this.conferenceClient != null) {
			let conferenceClient = this.conferenceClient;
			await conferenceClient.close()
				.catch((e: any) => {
					console.error("Error while closing conference client! Retrying the hard way.", e);
					conferenceClient.close(true);
				});
			this.vad?.destroy();
			this.gainController.inputTrack?.stop();
			this.conferenceClient = null;
			this.$video.classList.remove("mirrored");
		}
		// this.connectionStatus.status = "idle";
		await this.updatePublishedTracks(null, null, null);
		this.updateStateCssClasses();
		this.$video.srcObject = null;
		this._config.publishingParameters = null;
		this._config.playbackParameters = null;
	}

	private updatePromise: Promise<void> = Promise.resolve();

	update(config: UiMediaSoupV3WebRtcClientConfig): void {
		this.updatePromise = this.updatePromise.finally(() => {
			return this.updateInternal(config);
		})
	}

	async updateInternal(config: UiMediaSoupV3WebRtcClientConfig) {
		console.log("update()", config);
		this.$main.classList.toggle("activity-line-visible", config.activityLineVisible);
		this.$main.style.setProperty("--activity-line-inactive-color", config.activityInactiveColor);
		this.$main.style.setProperty("--activity-line-inactive-color", config.activityActiveColor);

		this.$bitrateDisplayWrapper.classList.toggle('hidden', !config.bitrateDisplayEnabled);

		if (!arraysEqual(config.toolButtons, this._config.toolButtons)) {
			this.$toolButtons.innerHTML = '';
			(config.toolButtons as UiToolButton[]).forEach(toolButton => {
				this.$toolButtons.append(toolButton.getMainElement());
			});
		}
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
		await this.updateConferenceClient(config);

		this._config = config;

		this.updateVideoVisibility();
		this.onResize();
	}

	private async updateConferenceClient(config: UiMediaSoupV3WebRtcClientConfig) {
		if (config.publishingParameters != null && config.playbackParameters != null) {
			console.error("Cannot publish and playback at the same time. Doing nothing!");
			return;
		}
		const roleChange = this._config.publishingParameters != null && config.playbackParameters != null
			|| this._config.playbackParameters != null && config.publishingParameters != null;
		if (roleChange) {
			await this.stop();
		}
		if (config.playbackParameters != null) {
			await this.updatePlayback(config.playbackParameters, this._config.forceRefreshCount != config.forceRefreshCount);
		} else if (config.publishingParameters != null) {
			await this.updatePublishing(config.publishingParameters, this._config.forceRefreshCount != config.forceRefreshCount);
		} else {
			this.connectionStatus.shouldHaveAudio = false;
			this.connectionStatus.shouldHaveVideo = false;
			await this.stop();
		}
		this._config.forceRefreshCount = config.forceRefreshCount;
	}

	async updatePlayback(newParams: UiMediaSoupPlaybackParametersConfig, forceRefresh: boolean) {
		this.connectionStatus.shouldHaveAudio = newParams.audio;
		this.connectionStatus.shouldHaveVideo = newParams.video;
		this.updateStateCssClasses();

		const oldParams = this._config.playbackParameters;
		const needsReset = forceRefresh
			|| oldParams?.server?.url !== newParams?.server?.url || oldParams?.server?.worker !== newParams?.server?.worker
			|| oldParams?.origin?.url !== newParams?.origin?.url || oldParams?.origin?.worker !== newParams?.origin?.worker
			|| oldParams?.streamUuid != newParams?.streamUuid;
		if (needsReset) {
			console.log("updatePlayback() --> needsReset", oldParams, newParams);
			await this.stop();

			if (newParams != null) {
				try {
					let conferenceApiConfig: ConferenceInput = {
						stream: newParams.streamUuid,
						token: newParams.server.token,
						url: newParams.server.url,
						worker: newParams.server.worker,
						origin: newParams.origin,
						kinds: [...(newParams.audio ? ["audio"] : []), ...(newParams.video ? ["video"] : [])] as MediaKind[],
						stopTracks: true
					};
					console.log("ConferenceApi config: ", conferenceApiConfig);
					this.conferenceClient = new ConferenceApi(conferenceApiConfig);
					this.registerStatuslListeners(this.conferenceClient, "playback");
					const mediaStream = await this.conferenceClient.subscribe();
					this.$video.muted = false;
					this.$video.srcObject = mediaStream;
					this.makeSafariVideoElementObserveMediaStreamTracks(mediaStream);

					this.startVideoPlayback(() => {
						console.log("Video playback successful.");
						this.onSubscribingSuccessful.fire({});
						this.$unmuteButtonWrapper.classList.add("hidden");
					}, (e) => {
						console.error("Video playback failed.");
						this.onSubscribingFailed.fire({errorMessage: e.toString()});
						this.$unmuteButtonWrapper.classList.remove("hidden");
					})
				} catch (error) {
					console.error("Error while starting playback!", error, error.stack);
					this.onSubscribingFailed.fire({errorMessage: error.toString()});
				}
			}
		} else {
			if (oldParams?.audio !== newParams.audio || oldParams.video !== newParams.video) {
				console.log("updatePlayback() --> needs updateKinds()", newParams);
				this.conferenceClient.updateKinds([...(newParams.audio ? ["audio"] : []), ...(newParams.video ? ["video"] : [])] as MediaKind[]);
			}
		}
	}

	private startVideoPlayback(successCallback: () => void, errorCallback: (e: Error) => void) {
		try {
			const playPromise = this.$video.play();
			if (playPromise != null) {
				playPromise.then(() => {
					successCallback();
				}).catch((e) => {
					errorCallback(e);
				})
			} else {
				successCallback();
			}
		} catch (e) {
			errorCallback(e);
		}
	}

	private makeSafariVideoElementObserveMediaStreamTracks(mediaStream: MediaStream) {
		if (Utils.isSafari) {
			["addtrack", "removetrack"].forEach(eventType => {
				this.conferenceClient.on(eventType as any, (trackEvent: MediaStreamTrackEvent) => {
					console.log(eventType);
					this.$video.srcObject = new MediaStream(mediaStream.getTracks());
					this.$video.play();
				});
			});
		}
	}

	async updatePublishing(newParams: UiMediaSoupPublishingParametersConfig, forceRefresh: boolean) {
		this.connectionStatus.shouldHaveAudio = newParams.audioConstraints != null;
		this.connectionStatus.shouldHaveVideo = newParams.videoConstraints != null;
		this.updateStateCssClasses();

		let oldParams = this._config.publishingParameters;

		const needsReset = forceRefresh
			|| oldParams?.server?.url != newParams?.server?.url
			|| oldParams?.streamUuid != newParams?.streamUuid
			|| oldParams?.simulcast !== newParams?.simulcast;

		if (needsReset) {
			console.log("updatePublishing() --> needsReset", oldParams, newParams);
			await this.stop();
			if (newParams != null) {
				this.targetStream = new MediaStream();
				this.$video.muted = true;
				this.$video.srcObject = this.targetStream;
				try {
					let conferenceApiConfig: ConferenceInput = {
						stream: newParams.streamUuid,
						token: newParams.server.token,
						url: newParams.server.url,
						worker: newParams.server.worker,
						simulcast: newParams.simulcast,
						stopTracks: false, // never stop the gainController's output track!
						keyFrameRequestDelay: newParams.keyFrameRequestDelay
					};
					console.log("ConferenceApi config: ", conferenceApiConfig);
					this.conferenceClient = new ConferenceApi(conferenceApiConfig);
					this.registerStatuslListeners(this.conferenceClient, "publishing");
					await this.conferenceClient.publish(this.targetStream);
					this.makeSafariVideoElementObserveMediaStreamTracks(this.targetStream);
				} catch (e) {
					console.error('Error while publishing!', e);
					if (e.statusCode) {
						console.error("HTTP status code: " + e.statusCode);
					}
					this.onTrackPublishingFailed.fire({
						audio: newParams.audioConstraints != null,
						video: newParams.videoConstraints != null,
						errorMessage: e.toString()
					});
					await this.stop();
					this.updateStateCssClasses();
				}
			}
		}

		await this.updatePublishedTracks(newParams.audioConstraints, newParams.videoConstraints, newParams.screenSharingConstraints);

		if (this.conferenceClient != null && newParams?.maxBitrate !== oldParams?.maxBitrate) {
			await this.conferenceClient.setMaxPublisherBitrate(newParams.maxBitrate);
		}

		if (this.targetStream.getTracks().length > 0) {
			this.$video.play(); // do not await - will hang for audio only (don't know why!)
		}
		this.$video.classList.toggle("mirrored", (newParams?.videoConstraints != null) && (newParams?.screenSharingConstraints == null));
	}

	private async updatePublishedTracks(
		newAudioConstraints: UiAudioTrackConstraintsConfig,
		newWebcamConstraints: UiVideoTrackConstraintsConfig,
		newScreenConstraints: UiScreenSharingConstraintsConfig) {

		let oldParams = this._config.publishingParameters;
		const oldAudioConstraints = oldParams?.audioConstraints;
		const oldWebcamConstraints = oldParams?.videoConstraints;
		const oldScreenConstraints = oldParams?.screenSharingConstraints;

		const audioConstraintsChanged = !deepEquals(oldAudioConstraints, newAudioConstraints);
		const webcamConstraintsChanged = !deepEquals(oldWebcamConstraints, newWebcamConstraints);
		const screenConstraintsChanged = !deepEquals(oldScreenConstraints, newScreenConstraints);

		if (audioConstraintsChanged) {
			console.log("updatePublishedTracks() --> audioConstraintsChanged", newAudioConstraints);
			this.vad?.destroy();
			this.gainController.inputTrack?.stop(); // this will release the microphone access
			if (newAudioConstraints != null) {
				try {
					let audioTracks = (await window.navigator.mediaDevices.getUserMedia({audio: newAudioConstraints, video: false})) // rejected if user denies!
						.getAudioTracks();
					const firstAudioTrack = audioTracks[0];
					for (let i = 1; i < audioTracks.length; i++) {
						audioTracks[i].stop(); // stop all but the first (should never be more than one actually!)
					}
					firstAudioTrack.addEventListener("ended", () => {
						if (this.gainController.inputTrack == firstAudioTrack) { // not intended stopping!
							this.onSourceMediaTrackEnded.fire({trackType: UiSourceMediaTrackType.MIC});
						}
					});
					this.gainController.inputTrack = firstAudioTrack;
					this.vad = addVoiceActivityDetection(firstAudioTrack, () => this.onVoiceActivityChanged.fire({active: true}), () => this.onVoiceActivityChanged.fire({active: false}));
				} catch (e) {
					console.error("Could not get user media: microphone!" + (location.protocol === "http:" ? " Probably due to plain HTTP (no encryption)." : ""), e);
					this.onSourceMediaTrackRetrievalFailed.fire({reason: UiMediaRetrievalFailureReason.MIC_MEDIA_RETRIEVAL_FAILED});
				}
			} else {
				this.gainController.inputTrack = null;
			}
		}
		this.gainController.gain = newAudioConstraints?.gainFactor ?? 0;

		if (webcamConstraintsChanged) {
			console.log("updatePublishedTracks() --> webcamConstraintsChanged", newWebcamConstraints);
			if (this.webcamTrack != null) {
				this.webcamTrack.stop();
				this.webcamTrack = null;
			}
			if (newWebcamConstraints != null) {
				try {
					let videoTracks = (await window.navigator.mediaDevices.getUserMedia({
						audio: false,
						video: createVideoConstraints(newWebcamConstraints)
					})) // rejected if user denies!
						.getVideoTracks();
					this.webcamTrack = videoTracks[0];
					for (let i = 1; i < videoTracks.length; i++) {
						videoTracks[i].stop(); // stop all but the first (should never be more than one actually!)
					}
					this.webcamTrack.addEventListener("ended", () => {
						if (this.webcamTrack != null) { // not intended stopping!
							this.onSourceMediaTrackEnded.fire({trackType: UiSourceMediaTrackType.CAM});
						}
					})
				} catch (e) {
					console.error("Could not get user media: camera!", e);
					this.onSourceMediaTrackRetrievalFailed.fire({reason: UiMediaRetrievalFailureReason.CAM_MEDIA_RETRIEVAL_FAILED});
				}
			}
		}

		if (screenConstraintsChanged) {
			console.log("updatePublishedTracks() --> screenConstraintsChanged", newScreenConstraints);
			if (this.screenTrack != null) {
				this.screenTrack.stop();
				this.screenTrack = null;
			}
			if (newScreenConstraints != null) {
				try {
					let screenTracks = (await getDisplayStream(newScreenConstraints)) // rejected if user denies!
						.getVideoTracks();
					this.screenTrack = screenTracks[0];
					for (let i = 1; i < screenTracks.length; i++) {
						screenTracks[i].stop(); // stop all but the first (should never be more than one actually!)
					}
					this.screenTrack.addEventListener("ended", () => {
						if (this.screenTrack != null) { // not intended stopping!
							this.onSourceMediaTrackEnded.fire({trackType: UiSourceMediaTrackType.SCREEN});
						}
					});
				} catch (e) {
					console.error("Could not get user media: screen!", e);
					this.onSourceMediaTrackRetrievalFailed.fire({reason: UiMediaRetrievalFailureReason.DISPLAY_MEDIA_RETRIEVAL_FAILED});
				}
			}
		}

		if (webcamConstraintsChanged || screenConstraintsChanged) {
			if (this.videoTrackMixer != null) {
				this.videoTrackMixer.close();
				this.videoTrackMixer = null;
			}

			if (this.screenTrack != null) { // Chrome does not do simulcast with screen sharing, so we need the mixer...
				try {
					let streamsWithMixSizingInfo: TrackWithMixSizingInfo[] = [];
					streamsWithMixSizingInfo.push({
						mediaTrack: this.screenTrack,
						mixSizingInfo: {fullCanvas: true}
					});

					if (this.webcamTrack) {
						let webcamMixSizingInfo: MixSizingInfo;
						const displayStreamDimensions = await determineVideoSize(new MediaStream([this.screenTrack]));
						const mainStreamShortDimension = Math.min(displayStreamDimensions.width, displayStreamDimensions.height);
						const cameraAspectRatio = this.webcamTrack.getSettings().aspectRatio || 4 / 3;
						const pictureInPictureHeight = Math.round((25 / 100) * mainStreamShortDimension);
						const pictureInPictureWidth = Math.round(pictureInPictureHeight * cameraAspectRatio);
						webcamMixSizingInfo = {
							width: pictureInPictureWidth,
							height: pictureInPictureHeight,
							right: 0,
							top: 0,
							// flipX: true
						};
						streamsWithMixSizingInfo.push({mediaTrack: this.webcamTrack, mixSizingInfo: webcamMixSizingInfo});
					}

					this.videoTrackMixer = await new VideoTrackMixer(streamsWithMixSizingInfo, 15);
				} catch (e) {
					this.onSourceMediaTrackRetrievalFailed.fire({reason: UiMediaRetrievalFailureReason.VIDEO_MIXING_FAILED});
				}
			}
		}

		if (this.conferenceClient != null) {
			if (oldAudioConstraints == null && newAudioConstraints != null) {
				try {
					await this.conferenceClient.addTrack(this.gainController.outputTrack);
					this.onTrackPublishingSuccessful.fire({audio: true, video: false});
				} catch (e) {
					console.error("Could not add audio track!", e);
					this.onTrackPublishingFailed.fire({audio: true, video: false, errorMessage: e.toString()})
				}
			} else if (oldAudioConstraints != null && newAudioConstraints == null) {
				this.targetStream.getAudioTracks().forEach(t => {
					this.conferenceClient.removeTrack(t);
				});
			}
			if (webcamConstraintsChanged || screenConstraintsChanged) {
				try {
					this.targetStream.getVideoTracks().forEach(t => {
						this.conferenceClient.removeTrack(t);
					});
					if (this.videoTrackMixer != null) {
						console.log("Adding this.videoTrackMixer.getMixedTrack()");
						await this.conferenceClient.addTrack(this.videoTrackMixer.getMixedTrack());
					} else if (this.webcamTrack != null) {
						console.log("Adding this.webcamTrack");
						await this.conferenceClient.addTrack(this.webcamTrack);
					} else if (this.screenTrack != null) {
						console.log("Adding this.screenTrack");
						await this.conferenceClient.addTrack(this.screenTrack);
					}
					if (this.videoTrackMixer != null || this.webcamTrack != null || this.screenTrack != null) {
						this.onTrackPublishingSuccessful.fire({video: true, audio: false});
					}
				} catch (e) {
					this.onTrackPublishingFailed.fire({video: true, audio: false, errorMessage: e.toString()})
				}
			}
		}
	}

	private registerStatuslListeners(conferenceClient: ConferenceApi, logPrefix: string) {
		conferenceClient.on('bitRate', ({bitRate, kind}: { bitRate: any, kind: any }) => {
			if (kind === 'audio') {
				this.$audioBitrateDisplay.innerText = (bitRate / 1000).toFixed(1) + "kb/s";
				this.connectionStatus.audioBitrate = bitRate;
				this.updateStateCssClasses();
				// console.log(`${logPrefix} audio bitrate: ${bitRate}`);
			} else if (kind == "video") {
				this.$videoBitrateDisplay.innerText = (bitRate / 1000).toFixed(1) + "kb/s";
				this.connectionStatus.videoBitrate = bitRate;
				this.updateStateCssClasses();
				// console.log(`${logPrefix} video bitrate: ${bitRate}`);
			}
		});
		conferenceClient.on('connectionstatechange', (event: any) => {
			let state: RTCPeerConnectionState = event.state;
			this.connectionStatus.rtcPeerConnectionState = state;
			this.updateStateCssClasses();
			this.onConnectionStateChanged.fire({connected: state === 'connected'});
		});
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
		let availableHeight = this.getHeight() - this.$caption.getBoundingClientRect().height; // don't use offsetHeight! It will flicker in Chrome!
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

		(this._config.toolButtons as UiToolButton[]).forEach(tb => tb.setIconSize(this.$toolsAndIconsBar.offsetHeight))
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

TeamAppsUiComponentRegistry.registerComponentClass("UiMediaSoupV3WebRtcClient", UiMediaSoupV3WebRtcClient);

