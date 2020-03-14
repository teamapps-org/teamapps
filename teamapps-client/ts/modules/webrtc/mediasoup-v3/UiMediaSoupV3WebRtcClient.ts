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
import {arraysEqual, calculateDisplayModeInnerSize, deepEquals, parseHtml, removeClassesByFunction} from "../../Common";
import {ContextMenu} from "../../micro-components/ContextMenu";
import {createVideoConstraints, enumerateDevices, getDisplayStream} from "../MediaUtil";
import {createUiColorCssString} from "../../util/CssFormatUtil";
import {UiPageDisplayMode} from "../../../generated/UiPageDisplayMode";
import {UiComponent} from "../../UiComponent";
import {ConferenceApi} from "./lib/front/src/conference-api";
import {WebRtcPublishingFailureReason} from "../../../generated/WebRtcPublishingFailureReason";
import {Utils} from "./lib/front/src/utils";
import {UiMediaSoupPlaybackParametersConfig} from "../../../generated/UiMediaSoupPlaybackParametersConfig";
import {UiMediaDeviceInfoConfig} from "../../../generated/UiMediaDeviceInfoConfig";
import {MediaKind} from "mediasoup-client/lib/RtpParameters";
import {UiAudioTrackConstraintsConfig} from "../../../generated/UiAudioTrackConstraintsConfig";
import {UiVideoTrackConstraintsConfig} from "../../../generated/UiVideoTrackConstraintsConfig";
import {UiScreenSharingConstraintsConfig} from "../../../generated/UiScreenSharingConstraintsConfig";
import {MixSizingInfo, TrackWithMixSizingInfo, VideoTrackMixer} from "../VideoTrackMixer";
import {determineVideoSize} from "../MultiStreamsMixer";

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

	private conferenceClient: ConferenceApi;
	private targetStream: MediaStream;

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
			});
		});

		this.update(config);
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	private updateStateCssClass() {
		removeClassesByFunction(this.$main.classList, className => className.startsWith("state-"));
		// if (this.connectionStatus.status === "idle") {
		// 	this.$main.classList.add("state-idle");
		// } else if (this.connectionStatus.status === "publish") {
		// if (!this.connectionStatus.connected || !this.connectionStatus.audioStatus || this.connectionStatus.audioBitrate == 0) {
		// 	this.$main.classList.add("state-connecting");
		// } else {
		// this.$main.classList.add("state-streaming");
		// }
		// } else if (this.connectionStatus.status === "playback") {
		// if (!this.connectionStatus.connected || this.connectionStatus.audioBitrate == 0) {
		// 	this.$main.classList.add("state-connecting");
		// } else {
		this.$main.classList.add("state-streaming");
		// }
		// } else if (this.connectionStatus.status === "error") {
		// 	this.$main.classList.add("state-error");
		// }
	}

	async stop() {
		console.log("stop()");
		if (this.conferenceClient != null) {
			let conferenceClient = this.conferenceClient;
			conferenceClient.close()
				.catch(e => {
					console.error("Error while closing conference client! Retrying the hard way.", e);
					conferenceClient.close(true);
				});
			this.conferenceClient = null;
			this.$video.classList.remove("mirrored");
		}
		// this.connectionStatus.status = "idle";
		await this.updatePublishedTracks(null, null, null);
		this.updateStateCssClass();
		this.$video.srcObject = null;
	}

	async update(config: UiMediaSoupV3WebRtcClientConfig) {
		console.log("update()", config);
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
		await this.updateConferenceClient(config);

		this._config = config;

		this.updateVideoVisibilities();
		this.onResize();
	}

	private async updateConferenceClient(config: UiMediaSoupV3WebRtcClientConfig) {
		console.log("updateConferenceClient()");
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
			await this.updatePlayback(config.playbackParameters);
		} else if (config.publishingParameters != null) {
			await this.updatePublishing(config.publishingParameters);
		} else {
			await this.stop();
		}
	}

	async updatePlayback(newParams: UiMediaSoupPlaybackParametersConfig) {
		console.log(`updatePlayback()`, newParams);

		const oldParams = this._config.playbackParameters;
		const needsReset = oldParams?.serverAddress != newParams?.serverAddress
			|| oldParams?.uid != newParams?.uid;

		if (needsReset) {
			this.stop();

			if (newParams != null) {
				try {
					this.conferenceClient = new ConferenceApi({
						stream: newParams.uid,
						token: newParams.token,
						url: newParams.serverAddress,
						kinds: [...(newParams.audio ? ["audio"] : []), ...(newParams.video ? ["video"] : [])] as MediaKind[]
					});
					const mediaStream = await this.conferenceClient.subscribe();
					this.$video.srcObject = mediaStream;

					const playVideoElement = async () => {
						this.$video.muted = false;
						this.$video.play().catch(error => {
							console.error('Unmuted autoplay failed!');
							this.onPlaybackFailed.fire({});
							this.$video.muted = true;
							this.$video.play().then(() => {
								console.log('autoplay successful, but only MUTED! TODO handle this gracefully!');
							});
						});
					};
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
					console.error("Error while starting playback!", error);
					this.onPlaybackFailed.fire({});
				}
			}
		} else {
			if (oldParams?.audio !== newParams.audio || oldParams.video !== newParams.video) {
				this.conferenceClient.updateKinds([...(newParams.audio ? ["audio"] : []), ...(newParams.video ? ["video"] : [])] as MediaKind[]);
			}
		}

		this.updateStateCssClass();
	}

	async updatePublishing(newParams: UiMediaSoupPublishingParametersConfig) {
		console.log("updatePublishing(): ", newParams);
		let oldParams = this._config.publishingParameters;

		const needsReset = oldParams?.serverAddress != newParams?.serverAddress
			|| oldParams?.uid != newParams?.uid
			|| oldParams?.simulcast !== newParams?.simulcast;

		if (needsReset) {
			this.stop();
			if (newParams != null) {
				this.targetStream = new MediaStream();
				try {
					this.conferenceClient = new ConferenceApi({
						stream: newParams.uid,
						token: newParams.token,
						url: newParams.serverAddress
					});
					await this.conferenceClient.publish(this.targetStream);
					this.$video.srcObject = this.targetStream;
					this.$video.muted = true;
					this.$video.play().catch(reason => { // DO NOT await here! It would not complete since adding tracks is later!
						console.log('Could not playback local (publishing) video!', reason);
					});
					this.onPublishingSucceeded.fire({}); // TODO move somewhere better - maybe as a result of a fired event
				} catch (e) {
					console.error('Error while publishing!', e);
					if (e.statusCode) {
						console.error("HTTP status code: " + e.statusCode);
					}
					this.onPublishingFailed.fire({errorMessage: e.exception.toString(), reason: WebRtcPublishingFailureReason.CONNECTION_ESTABLISHMENT_FAILED});
					this.stop();
					// this.connectionStatus.status = "error";
					this.updateStateCssClass();
				}
			}
		}

		await this.updatePublishedTracks(newParams.audioConstraints, newParams.videoConstraints, newParams.screenSharingConstraints);

		this.$video.classList.toggle("mirrored", newParams?.videoConstraints && !(newParams?.screenSharingConstraints));
	}

	private audioTrack: MediaStreamTrack;
	private webcamTrack: MediaStreamTrack;
	private screenTrack: MediaStreamTrack;
	private videoTrackMixer: VideoTrackMixer;

	private async updatePublishedTracks(
		newAudioConstraints: UiAudioTrackConstraintsConfig,
		newWebcamConstraints: UiVideoTrackConstraintsConfig,
		newScreenConstraints: UiScreenSharingConstraintsConfig) {

		let oldParams = this._config.publishingParameters;
		const oldAudioConstraints = oldParams?.audioConstraints ?? null;
		const oldWebcamConstraints = oldParams?.videoConstraints ?? null;
		const oldScreenConstraints = oldParams?.screenSharingConstraints ?? null;

		const audioConstraintsChanged = !deepEquals(oldAudioConstraints, newAudioConstraints);
		const webcamConstraintsChanged = !deepEquals(oldWebcamConstraints, newWebcamConstraints);
		const screenConstraintsChanged = !deepEquals(oldScreenConstraints, newScreenConstraints);

		if (audioConstraintsChanged) {
			if (this.audioTrack != null) {
				this.audioTrack.stop();
				this.audioTrack = null;
			}
			if (newAudioConstraints != null) {
				try {
					let audioTracks = (await window.navigator.mediaDevices.getUserMedia({audio: newAudioConstraints, video: false})) // rejected if user denies!
						.getAudioTracks();
					this.audioTrack = audioTracks[0];
					for (let i = 1; i < audioTracks.length; i++) {
						audioTracks[i].stop(); // stop all but the first (should never be more than one actually!)
					}
					this.audioTrack.addEventListener("ended", () => {
						if (this.audioTrack != null) { // not intended stopping!
							/*TODO handle track ended*/
						}
					})
				} catch (e) {
					throw {
						exception: e,
						reason: WebRtcPublishingFailureReason.MIC_MEDIA_RETRIEVAL_FAILED
					};
				}
			}
		}

		if (webcamConstraintsChanged) {
			if (this.webcamTrack != null) {
				this.webcamTrack.stop();
				this.webcamTrack = null;
			}
			if (newWebcamConstraints != null) {
				console.log("WILL ATTEMPT TO GET VIDEO USER MEDIA!");
				try {
					let videoTracks = (await window.navigator.mediaDevices.getUserMedia({audio: false, video: createVideoConstraints(newWebcamConstraints)})) // rejected if user denies!
						.getVideoTracks();
					this.webcamTrack = videoTracks[0];
					for (let i = 1; i < videoTracks.length; i++) {
						videoTracks[i].stop(); // stop all but the first (should never be more than one actually!)
					}
					this.webcamTrack.addEventListener("ended", () => {
						if (this.webcamTrack != null) { // not intended stopping!
							/*TODO handle track ended*/
						}
					})
				} catch (e) {
					throw {
						exception: e,
						reason: WebRtcPublishingFailureReason.CAM_MEDIA_RETRIEVAL_FAILED
					};
				}
			}
		}

		if (screenConstraintsChanged) {
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
							/*TODO handle track ended*/
						}
					})
				} catch (e) {
					throw {
						exception: e,
						reason: WebRtcPublishingFailureReason.DISPLAY_MEDIA_RETRIEVAL_FAILED
					};
				}
			}
		}

		if (webcamConstraintsChanged || screenConstraintsChanged) {
			if (this.videoTrackMixer != null) {
				this.videoTrackMixer.close();
				this.videoTrackMixer = null;
			}

			if (this.webcamTrack != null && this.screenTrack != null) {
				try {
					let streamsWithMixSizingInfo: TrackWithMixSizingInfo[] = [];
					streamsWithMixSizingInfo.push({
						mediaTrack: this.screenTrack,
						mixSizingInfo: {fullCanvas: true}
					});

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
						top: 0
					};
					streamsWithMixSizingInfo.push({mediaTrack: this.webcamTrack, mixSizingInfo: webcamMixSizingInfo});

					this.videoTrackMixer = await new VideoTrackMixer(streamsWithMixSizingInfo, 15);
				} catch (e) {
					throw {
						exception: e,
						reason: WebRtcPublishingFailureReason.VIDEO_MIXING_FAILED
					};
				}
			}
		}

		if (this.conferenceClient != null) {
			if (audioConstraintsChanged) {
				this.targetStream.getAudioTracks().forEach(t => this.conferenceClient.removeTrack(t));
				if (this.audioTrack != null) {
					await this.conferenceClient.addTrack(this.audioTrack);
				}
			}
			if (webcamConstraintsChanged || screenConstraintsChanged) {
				this.targetStream.getVideoTracks().forEach(t => this.conferenceClient.removeTrack(t));
				if (this.videoTrackMixer != null) {
					await this.conferenceClient.addTrack(this.videoTrackMixer.getMixedTrack());
				} else if (this.webcamTrack != null) {
					await this.conferenceClient.addTrack(this.webcamTrack);
				} else if (this.screenTrack != null) {
					await this.conferenceClient.addTrack(this.screenTrack);
				}
			}
		}
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

	public static async enumerateDevices(): Promise<UiMediaDeviceInfoConfig[]> {
		return enumerateDevices();
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiMediaSoupV3WebRtcClient", UiMediaSoupV3WebRtcClient);

