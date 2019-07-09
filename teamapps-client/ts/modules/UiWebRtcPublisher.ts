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

import {UiAudioActivityDisplay} from "./micro-components/UiAudioActivityDisplay";
import {UiWebRtcPublisher_PublishingFailedEvent, UiWebRtcPublisherCommandHandler, UiWebRtcPublisherConfig, UiWebRtcPublisherEventSource} from "../generated/UiWebRtcPublisherConfig";
import {UiSpinner} from "./micro-components/UiSpinner";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiPageDisplayMode} from "../generated/UiPageDisplayMode";
import {UiAudioCodec} from "../generated/UiAudioCodec";
import {UiVideoCodec} from "../generated/UiVideoCodec";
import {applyDisplayMode, parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiWebRtcPublishingSettingsConfig} from "../generated/UiWebRtcPublishingSettingsConfig";
import {checkChromeExtensionAvailable, getScreenConstraints, isChrome} from "./util/ScreenCapturing";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiWebRtcPublishingErrorReason} from "../generated/UiWebRtcPublishingErrorReason";
import {MediaStreamWithMixiSizingInfo, MultiStreamsMixer} from "./util/MultiStreamsMixer";
import {UiWebRtcPublishingMediaSettingsConfig} from "../generated/UiWebRtcPublishingMediaSettingsConfig";

type WebRtcState = 'new' | 'checking' | 'connected' | 'completed' | 'failed' | 'disconnected' | 'closed';

interface SdpDescriptorEnhancingData {
	audioBitrate: number,
	videoBitrate: number,
	videoFrameRate: number
}

export class UiWebRtcPublisher extends AbstractUiComponent<UiWebRtcPublisherConfig> implements UiWebRtcPublisherCommandHandler, UiWebRtcPublisherEventSource {

	private static readonly PEER_CONNECTION_CONFIG: any = {'iceServers': []};

	public readonly onPublishingFailed: TeamAppsEvent<UiWebRtcPublisher_PublishingFailedEvent> = new TeamAppsEvent(this);

	private $main: HTMLElement;
	private $videoContainer: HTMLElement;
	private video: HTMLVideoElement;
	private $audioActivityDisplayContainer: HTMLElement;
	private $spinnerContainer: HTMLElement;

	private publishingSettings: UiWebRtcPublishingSettingsConfig;
	private publishingSignalingWsConnection: WebSocket = null;
	private publishingPeerConnection: RTCPeerConnection = null;
	private publishingUserData: { param1: string };
	private publishingIceConnectionState: WebRtcState;

	private audioActivityDisplay: UiAudioActivityDisplay;
	private multiStreamsMixer: MultiStreamsMixer;
	private microphoneMuted: boolean;

	constructor(config: UiWebRtcPublisherConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`
<div class="UiWebRtcPublisher">
  <div class="video-container">
    <video autoplay></video>
    <div class="mic-muted-icon"></div>
    <div class="audio-activity-display-container hidden"></div>
	<div class="spinner-container hidden"></div>	
  </div> 
</div>`);
		this.video = <HTMLVideoElement> this.$main.querySelector<HTMLElement>(':scope video');
		this.video.addEventListener("abort", () => { // this event will be triggered when stopPlaying() is invoked.
			this.video.load(); // show poster again. We cannot do this directly in stopPlaying()...
		});
		this.video.onloadedmetadata = this.onResize.bind(this);
		this.$videoContainer = this.$main.querySelector<HTMLElement>(':scope .video-container');
		this.$audioActivityDisplayContainer = this.$main.querySelector<HTMLElement>(':scope .audio-activity-display-container');
		this.audioActivityDisplay = new UiAudioActivityDisplay();
		this.$audioActivityDisplayContainer.append(this.audioActivityDisplay.getMainDomElement());
		this.$spinnerContainer = this.$main.querySelector<HTMLElement>(':scope .spinner-container');
		this.$spinnerContainer.append(new UiSpinner().getMainDomElement());

		if (config.publishingSettings != null) {
			this.publish(config.publishingSettings);
		}

		this.setMicrophoneMuted(config.microphoneMuted);

		this.setBackgroundImageUrl(config.backgroundImageUrl);
	}

	public publish(settings: UiWebRtcPublishingSettingsConfig) {
		this.unPublish();

		this.publishingSettings = settings;

		this.getMediaStreamMixer(settings.mediaSettings)
			.then((multiStreamsMixer: MultiStreamsMixer) => {
				this.multiStreamsMixer = multiStreamsMixer;
				this.setMicrophoneMuted(this.microphoneMuted);
				if (this.multiStreamsMixer.getMixedStream().getTracks().some(f => f.kind === "audio")) {
					this.audioActivityDisplay.bindToStream(this.multiStreamsMixer.getMixedStream());
				}
				this.video.muted = true;
				try {
					this.video.srcObject = multiStreamsMixer.getMixedStream();
				} catch (error) {
					this.video.src = window.URL.createObjectURL(multiStreamsMixer);
				}

				this.reconnectForPublishing();
			})
			.catch((reason: any) => {
				if (reason !== UiWebRtcPublishingErrorReason.CHROME_SCREEN_SHARING_EXTENSION_NOT_INSTALLED) {
					reason = UiWebRtcPublishingErrorReason.OTHER;
				}
				this.onPublishingFailed.fire({
					reason: reason
				});
				console.error("Could not create screen sharing MediaStream. Reason:", UiWebRtcPublishingErrorReason[reason]);
			});
	}

	setMicrophoneMuted(microphoneMuted: boolean): void {
		this.microphoneMuted = microphoneMuted;
		if (this.multiStreamsMixer) {
			this.multiStreamsMixer.getInputMediaStreams().forEach(inputStream => inputStream.getAudioTracks().forEach(track => track.enabled = !this.microphoneMuted));
		}
		this.audioActivityDisplay.getMainDomElement().classList.toggle("hidden", microphoneMuted);
	}

	private async getMediaStreamMixer(publishingSettings: UiWebRtcPublishingMediaSettingsConfig) {

		let screenStream: MediaStream = null;
		let camMicStream: MediaStream = null;

		if (publishingSettings.screen) {
			const chromeExtensionInstalled = await checkChromeExtensionAvailable();
			this.logger.info("Chrome extension available: " + chromeExtensionInstalled);
			const canProbablyPublishScreen = !isChrome || chromeExtensionInstalled;
			if (canProbablyPublishScreen) {
				const screenConstraints = await getScreenConstraints();
				try {
					screenStream = await navigator.mediaDevices.getUserMedia({video: screenConstraints});
				} catch (e) {
					throw UiWebRtcPublishingErrorReason.CANNOT_GET_SCREEN_MEDIA_STREAM;
				}
			} else {
				throw UiWebRtcPublishingErrorReason.CHROME_SCREEN_SHARING_EXTENSION_NOT_INSTALLED;
			}
		}

		if (publishingSettings.video || publishingSettings.audio) {
			let constraints = {
				video: publishingSettings.video ? {
					width: publishingSettings.videoWidth,
					height: publishingSettings.videoHeight
				} : false,
				audio: publishingSettings.audio
			};
			camMicStream = await navigator.mediaDevices.getUserMedia(constraints);
			// setTimeout(() => {
			// 	normalStream.getTracks().forEach(t => t.stop())
			// }, 5000)
		}

		if (screenStream != null && camMicStream != null) {
			const screenStreamDimensions = await UiWebRtcPublisher.determineVideoSize(screenStream);
			const screenStreamWithSizingInfo = screenStream as MediaStreamWithMixiSizingInfo;
			screenStreamWithSizingInfo.fullcanvas = true;
			screenStreamWithSizingInfo.width = screenStreamDimensions.width;
			screenStreamWithSizingInfo.height = screenStreamDimensions.height;

			if (camMicStream.getTracks().some(t => t.kind === "video")) {
				const screenStreamShortDimension = Math.min(screenStreamDimensions.width, screenStreamDimensions.height);
				const cameraAspectRatio = camMicStream.getTracks().filter(t => t.kind === "video")[0].getSettings().aspectRatio;
				const pictureInPictureHeight = Math.round((25 / 100) * screenStreamShortDimension);
				const pictureInPictureWidth = Math.round(pictureInPictureHeight * cameraAspectRatio);

				const camMicStreamWithSizingInfo = camMicStream as MediaStreamWithMixiSizingInfo;
				camMicStreamWithSizingInfo.width = pictureInPictureWidth;
				camMicStreamWithSizingInfo.height = pictureInPictureHeight;
				camMicStreamWithSizingInfo.left = screenStreamDimensions.width - pictureInPictureWidth;
				camMicStreamWithSizingInfo.top = 0; // screenStreamDimensions.height - pictureInPictureHeight;
			}

			let multiStreamsMixer = new MultiStreamsMixer([screenStreamWithSizingInfo, camMicStream]);
			multiStreamsMixer.frameInterval = 1000 / screenStream.getTracks()[0].getSettings().frameRate;
			return multiStreamsMixer;
		} else if (camMicStream != null) {
			return new MultiStreamsMixer([camMicStream]);
		} else if (screenStream != null) {
			let multiStreamsMixer = new MultiStreamsMixer([screenStream]);
			multiStreamsMixer.frameInterval = 1000 / screenStream.getTracks()[0].getSettings().frameRate;
			return multiStreamsMixer;
		}

	}

	private static determineVideoSize(mediaStream: MediaStream, timeout = 1000): Promise<{ width: number, height: number }> {
		return new Promise((resolve, reject) => {
			var video: HTMLVideoElement = parseHtml('<video class="pseudo-hidden"></video>');
			document.body.appendChild(video);
			video.srcObject = mediaStream;
			video.muted = true;
			video.onloadedmetadata = (e) => {
				resolve({width: video.videoWidth, height: video.videoHeight});
				video.remove();
			};
			setTimeout(() => {
				reject();
				video.remove();
			}, timeout);
		});
	}

	private reconnectForPublishing() {
		if (!this.publishingSettings) {
			return;
		}

		this.publishingSignalingWsConnection = new WebSocket(this.publishingSettings.signalingUrl);
		this.publishingSignalingWsConnection.binaryType = 'arraybuffer';
		this.publishingSignalingWsConnection.onopen = () => {
			this.logger.debug("this.playingSignalingWsConnection.onopen");
			this.publishingPeerConnection = new RTCPeerConnection(UiWebRtcPublisher.PEER_CONNECTION_CONFIG);
			this.publishingPeerConnection.onicecandidate = this.gotIceCandidate.bind(this);
			this.publishingPeerConnection.oniceconnectionstatechange = this.onPublishingIceConnectionStateChange.bind(this);
			(this.publishingPeerConnection as any).addStream(this.multiStreamsMixer.getMixedStream());
			this.publishingPeerConnection.createOffer()
				.then(sdpDescriptionInit => {
					const enhancingData: SdpDescriptorEnhancingData = {
						audioBitrate: Number(this.publishingSettings.mediaSettings.audioKiloBitsPerSecond),
						videoBitrate: Number(this.publishingSettings.mediaSettings.videoKiloBitsPerSecond),
						videoFrameRate: Number(this.publishingSettings.mediaSettings.videoFps)
					};

					this.logger.debug('offer created: ' + JSON.stringify({'sdp': sdpDescriptionInit}));
					// console.log(sdpDescriptionInit.sdp);
					sdpDescriptionInit.sdp = this.enhanceSDP(sdpDescriptionInit.sdp, enhancingData);
					// console.log("==========================");
					// console.log(sdpDescriptionInit.sdp);
					this.logger.debug('offer enhanced: ' + JSON.stringify(sdpDescriptionInit.sdp));

					this.publishingPeerConnection.setLocalDescription(sdpDescriptionInit)
						.then(() => {
							let streamInfo = {
								applicationName: this.publishingSettings.wowzaApplicationName,
								streamName: this.publishingSettings.streamName,
								sessionId: "[empty]"
							};
							this.publishingUserData = {param1: "value1"};
							this.publishingSignalingWsConnection.send('{"direction":"publish", "command":"sendOffer", "streamInfo":' + JSON.stringify(streamInfo) + ', "sdp":' + JSON.stringify(sdpDescriptionInit) + ', "userData":' + JSON.stringify(this.publishingUserData) + '}');
						}, (error) => {
							this.logger.error('set description error', error)
						});
				}, this.errorHandler.bind(this));
		};
		this.publishingSignalingWsConnection.onmessage = (evt) => {
			this.logger.debug("this.playingSignalingWsConnection.onmessage: " + evt.data);
			const msgJSON = JSON.parse(evt.data);
			const msgStatus = Number(msgJSON['status']);
			this.logger.info(`Status: ${msgStatus} - ${msgJSON['statusDescription']}`);
			if (msgStatus == 200) {
				if (msgJSON.sdp !== undefined) {
					this.logger.debug('Received sdp: ' + JSON.stringify(msgJSON['sdp']));
					this.publishingPeerConnection.setRemoteDescription(new RTCSessionDescription(msgJSON.sdp))
						.then(() => {
							//peerConnection.createAnswer(gotDescription, errorHandler);
						}, this.errorHandler.bind(this));
				}
				const iceCandidates = msgJSON['iceCandidates'];
				if (iceCandidates !== undefined) {
					for (let index in iceCandidates) {
						this.logger.debug('iceCandidates: ' + JSON.stringify(iceCandidates[index]));
						this.publishingPeerConnection.addIceCandidate(new RTCIceCandidate(iceCandidates[index]));
					}
				}
			} else {
				this.logger.error(msgJSON.statusDescription);
				setTimeout(() => this.reconnectForPublishing(), 1000);
			}
			if (this.publishingSignalingWsConnection != null) {
				this.publishingSignalingWsConnection.close();
			}
			this.publishingSignalingWsConnection = null;
		};
		this.publishingSignalingWsConnection.onclose = () => {
			this.logger.debug("this.playingSignalingWsConnection.onclose");
		};
		this.publishingSignalingWsConnection.onerror = (evt) => {
			this.logger.error('WebSocket connection failed: ' + this.publishingSettings.signalingUrl);
			this.logger.debug("this.playingSignalingWsConnection.onerror: " + JSON.stringify(evt));
			setTimeout(() => this.reconnectForPublishing(), 1000);
		};
		this.updateUi();
	}

	public unPublish() {
		this.publishingSettings = null;
		if (this.publishingPeerConnection != null) {
			this.publishingPeerConnection.close();
		}
		this.publishingPeerConnection = null;

		if (this.publishingSignalingWsConnection != null) {
			this.publishingSignalingWsConnection.close();
		}
		this.publishingSignalingWsConnection = null;
		this.video.src = "";
		this.video.srcObject = null;
		this.video.muted = false;
		if (this.multiStreamsMixer != null) {
			this.multiStreamsMixer.close();
		}
	}

	private gotIceCandidate(event: RTCPeerConnectionIceEvent) {
		if (event.candidate != null) {
			this.logger.debug('gotIceCandidate: ' + JSON.stringify({'ice': event.candidate}));
		}
	}

	private onPublishingIceConnectionStateChange(event: Event) {
		let peerConnection = event.currentTarget as RTCPeerConnection;
		let state = peerConnection.iceConnectionState;
		this.publishingIceConnectionState = state;
		this.logger.debug("publishingIceConnectionState: " + state);

		if (state === "closed") {
			this.audioActivityDisplay.unbind();
		}
		if (state === "failed") {
			setTimeout(() => this.reconnectForPublishing(), 1000);
		}

		this.updateUi();
	}

	private updateUi() {
		const nonLoadingIceStates: WebRtcState[] = ['connected', 'completed', 'closed'];
		let loading: boolean = this.publishingSignalingWsConnection != null || nonLoadingIceStates.indexOf(this.publishingIceConnectionState) === -1;

		this.$spinnerContainer.classList.toggle("hidden", !loading);
		this.$audioActivityDisplayContainer.classList.toggle("hidden", this.publishingIceConnectionState !== "completed");
	}

	private addAudio(sdpStr: string, audioLine: string) {
		var sdpLines = sdpStr.split(/\r\n/);
		var sdpStrRet = '';
		var done = false;

		for (var sdpIndex in sdpLines) {
			var sdpLine = sdpLines[sdpIndex];

			if (sdpLine.length <= 0)
				continue;


			sdpStrRet += sdpLine;
			sdpStrRet += '\r\n';

			if ('a=rtcp-mux'.localeCompare(sdpLine) == 0 && done == false) {
				sdpStrRet += audioLine;
				done = true;
			}


		}
		return sdpStrRet;
	}

	private addVideo(sdpStr: string, videoLine: string) {
		var sdpLines = sdpStr.split(/\r\n/);
		var sdpSection = 'header';
		var hitMID = false;
		var sdpStrRet = '';
		var done = false;

		var rtcpSize = false;
		var rtcpMux = false;

		for (var sdpIndex in sdpLines) {
			var sdpLine = sdpLines[sdpIndex];

			if (sdpLine.length <= 0)
				continue;

			if (sdpLine.includes("a=rtcp-rsize")) {
				rtcpSize = true;
			}

			if (sdpLine.includes("a=rtcp-mux")) {
				rtcpMux = true;
			}

		}

		for (var sdpIndex in sdpLines) {
			var sdpLine = sdpLines[sdpIndex];

			sdpStrRet += sdpLine;
			sdpStrRet += '\r\n';

			if (('a=rtcp-rsize'.localeCompare(sdpLine) == 0) && done == false && rtcpSize == true) {
				sdpStrRet += videoLine;
				done = true;
			}

			if ('a=rtcp-mux'.localeCompare(sdpLine) == 0 && done == true && rtcpSize == false) {
				sdpStrRet += videoLine;
				done = true;
			}

			if ('a=rtcp-mux'.localeCompare(sdpLine) == 0 && done == false && rtcpSize == false) {
				done = true;
			}

		}
		return sdpStrRet;
	}

	private enhanceSDP(sdpStr: string, enhanceData: SdpDescriptorEnhancingData) {
		let sdpLines = sdpStr.split(/\r\n/);
		let sdpSection = 'header';
		let hitMID = false;
		let sdpStrRet = '';

		//console.log("Original SDP: "+sdpStr);

		// Firefox provides a reasonable SDP, Chrome is just odd
		// so we have to doing a little mundging to make it all work
		let audioIndex: number;
		let videoIndex: number;
		if (!sdpStr.includes("THIS_IS_SDPARTA") || this.publishingSettings.mediaSettings.videoCodec === UiVideoCodec.VP9) {
			let sdpOutput = {};
			for (let sdpIndex in sdpLines) {
				const sdpLine = sdpLines[sdpIndex];

				if (sdpLine.length <= 0)
					continue;

				var doneCheck = this.checkLine(sdpLine, sdpOutput);
				if (!doneCheck)
					continue;

				sdpStrRet += sdpLine;
				sdpStrRet += '\r\n';

			}
			let audio: string;
			({outputString: audio, line: audioIndex} = this.deliverCheckLine(UiAudioCodec[this.publishingSettings.mediaSettings.audioCodec], "audio", sdpOutput));
			sdpStrRet = this.addAudio(sdpStrRet, audio);
			let video: string;
			({outputString: video, line: videoIndex} = this.deliverCheckLine(UiVideoCodec[this.publishingSettings.mediaSettings.videoCodec], "video", sdpOutput));
			sdpStrRet = this.addVideo(sdpStrRet, video);
			sdpStr = sdpStrRet;
			sdpLines = sdpStr.split(/\r\n/);
			sdpStrRet = '';
		}

		for (var sdpIndex in sdpLines) {
			var sdpLine = sdpLines[sdpIndex];

			if (sdpLine.length <= 0)
				continue;

			if (sdpLine.indexOf("m=audio") == 0 && audioIndex != -1) {
				let audioMLines = sdpLine.split(" ");
				sdpStrRet += audioMLines[0] + " " + audioMLines[1] + " " + audioMLines[2] + " " + audioIndex + "\r\n";
				continue;
			}

			if (sdpLine.indexOf("m=video") == 0 && videoIndex != -1) {
				let videoMLines = sdpLine.split(" ");
				sdpStrRet += videoMLines[0] + " " + videoMLines[1] + " " + videoMLines[2] + " " + videoIndex + "\r\n";
				continue;
			}
			sdpStrRet += sdpLine;

			if (sdpLine.indexOf("m=audio") === 0) {
				sdpSection = 'audio';
				hitMID = false;
			} else if (sdpLine.indexOf("m=video") === 0) {
				sdpSection = 'video';
				hitMID = false;
			} else if (sdpLine.indexOf("a=rtpmap") == 0) {
				sdpSection = 'bandwidth';
				hitMID = false;
			}

			if (sdpLine.indexOf("a=mid:") === 0 || sdpLine.indexOf("a=rtpmap") == 0) {
				if (!hitMID) {
					if ('audio'.localeCompare(sdpSection) == 0) {
						if (enhanceData.audioBitrate !== undefined) {
							sdpStrRet += '\r\nb=CT:' + (enhanceData.audioBitrate);
							sdpStrRet += '\r\nb=AS:' + (enhanceData.audioBitrate);
						}
						hitMID = true;
					} else if ('video'.localeCompare(sdpSection) == 0) {
						if (enhanceData.videoBitrate !== undefined) {
							sdpStrRet += '\r\nb=CT:' + (enhanceData.videoBitrate);
							sdpStrRet += '\r\nb=AS:' + (enhanceData.videoBitrate);
							if (enhanceData.videoFrameRate !== undefined) {
								sdpStrRet += '\r\na=framerate:' + enhanceData.videoFrameRate;
							}
						}
						hitMID = true;
					} else if ('bandwidth'.localeCompare(sdpSection) == 0) {
						let rtpmapID;
						rtpmapID = this.getRtpMapID(sdpLine);
						if (rtpmapID !== null) {
							const match = rtpmapID[2].toLowerCase();
							if (('vp9'.localeCompare(match) == 0) || ('vp8'.localeCompare(match) == 0) || ('h264'.localeCompare(match) == 0) ||
								('red'.localeCompare(match) == 0) || ('ulpfec'.localeCompare(match) == 0) || ('rtx'.localeCompare(match) == 0)) {
								if (enhanceData.videoBitrate !== undefined) {
									sdpStrRet += '\r\na=fmtp:' + rtpmapID[1] + ' x-google-min-bitrate=' + (enhanceData.videoBitrate) + ';x-google-max-bitrate=' + (enhanceData.videoBitrate);
								}
							}

							if (('opus'.localeCompare(match) == 0) || ('isac'.localeCompare(match) == 0) || ('g722'.localeCompare(match) == 0) || ('pcmu'.localeCompare(match) == 0) ||
								('pcma'.localeCompare(match) == 0) || ('cn'.localeCompare(match) == 0)) {
								if (enhanceData.audioBitrate !== undefined) {
									sdpStrRet += '\r\na=fmtp:' + rtpmapID[1] + ' x-google-min-bitrate=' + (enhanceData.audioBitrate) + ';x-google-max-bitrate=' + (enhanceData.audioBitrate);
								}
							}
						}
					}
				}
			}
			sdpStrRet += '\r\n';
		}
		// this.logger.debug("Resulting SDP (offer): "+sdpStrRet);
		return sdpStrRet;
	}

	private deliverCheckLine(profile: string, type: "audio" | "video", SDPOutput: { [x: number]: string }): {
		outputString: string,
		line: number
	} {

		if (profile === "H264") {
			profile = "42e01f";
		}

		var outputString = "";
		for (var line in SDPOutput) {
			var lineInUse = SDPOutput[line];
			outputString += lineInUse;
			if (type === "audio" && lineInUse.toLowerCase().includes((profile.toLowerCase()))
				|| type === "video" && lineInUse.toLowerCase().includes(profile.toLowerCase())) {
				if (profile === "VP9" || profile === "VP8") {
					var output = "";
					var outputs = lineInUse.split(/\r\n/);
					for (var position in outputs) {
						var transport = outputs[position];
						if (transport.indexOf("transport-cc") !== -1 || transport.indexOf("goog-remb") !== -1 || transport.indexOf("nack") !== -1) {
							continue;
						}
						output += transport;
						output += "\r\n";
					}
					return {outputString: output, line: parseInt(line)};
				}
				return {outputString: lineInUse, line: parseInt(line)};
			}
		}
		return {outputString: "", line: -1};
	}

	private checkLine(line: string, SDPOutput: { [x: number]: string }) {
		if (line.startsWith("a=rtpmap") || line.startsWith("a=rtcp-fb") || line.startsWith("a=fmtp")) {
			var res = line.split(":");

			if (res.length > 1) {
				var number = res[1].split(" ");
				if (!isNaN(number[0] as any)) // TODO use parseInt!!
				{
					if (!number[1].startsWith("http") && !number[1].startsWith("ur")) {
						var currentString = SDPOutput[number[0] as any as number];
						if (!currentString) {
							currentString = "";
						}
						currentString += line + "\r\n";
						SDPOutput[number[0] as any as number] = currentString;
						return false;
					}
				}
			}
		}

		return true;
	}

	private getRtpMapID(line: string) {
		const findid = new RegExp('a=rtpmap:(\\d+) (\\w+)/(\\d+)');
		const found = line.match(findid);
		return (found && found.length >= 3) ? found : null;
	}

	private errorHandler(error: any) {
		this.logger.error(error);
	}

	public setBackgroundImageUrl(backgroundImageUrl: string) {
		this.video.style.backgroundImage = `url(${backgroundImageUrl})`;
	}

	public onResize(): void {
		applyDisplayMode(this.$main, this.$videoContainer, UiPageDisplayMode.FIT_SIZE, {
			innerPreferedDimensions: {
				width: this.video.videoWidth,
				height: this.video.videoHeight
			}
		});
	}

	destroy(): void {
		this.unPublish();
	}

	getMainDomElement(): HTMLElement {
		return this.$main;
	}

}

navigator.getUserMedia = navigator.getUserMedia || (navigator as any).mozGetUserMedia || (navigator as any).webkitGetUserMedia;
(window as any).RTCPeerConnection = (window as any).RTCPeerConnection || (window as any).mozRTCPeerConnection || (window as any).webkitRTCPeerConnection;
(window as any).RTCIceCandidate = (window as any).RTCIceCandidate || (window as any).mozRTCIceCandidate || (window as any).webkitRTCIceCandidate;
(window as any).RTCSessionDescription = (window as any).RTCSessionDescription || (window as any).mozRTCSessionDescription || (window as any).webkitRTCSessionDescription;

TeamAppsUiComponentRegistry.registerComponentClass("UiWebRtcPublisher", UiWebRtcPublisher);
