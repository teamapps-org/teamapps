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

import {UiWebRtcPlayerCommandHandler, UiWebRtcPlayerConfig} from "../generated/UiWebRtcPlayerConfig";
import {UiSpinner} from "./micro-components/UiSpinner";
import {AbstractUiComponent} from "teamapps-client-core";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {applyDisplayMode, parseHtml} from "./Common";
import {UiPageDisplayMode} from "../generated/UiPageDisplayMode";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiWebRtcPlayingSettingsConfig} from "../generated/UiWebRtcPlayingSettingsConfig";
import {getPlayableVideoCodecs} from "./util/getPlayableVideoCodecs";
import {UiVideoCodec} from "../generated/UiVideoCodec";

type WebRtcState = 'new' | 'checking' | 'connected' | 'completed' | 'failed' | 'disconnected' | 'closed';

export class UiWebRtcPlayer extends AbstractUiComponent<UiWebRtcPlayerConfig> implements UiWebRtcPlayerCommandHandler {

	private static readonly PEER_CONNECTION_CONFIG: any = {'iceServers': []};

	private $main: HTMLElement;
	private $videoContainer: HTMLElement;
	private $audioActivityDisplayContainer: HTMLElement;
	private $spinnerContainer: HTMLElement;

	private remoteVideo: HTMLVideoElement;
	private peerConnection: RTCPeerConnection = null;
	private settings: UiWebRtcPlayingSettingsConfig;
	private signalingWsConnection: WebSocket = null;
	private streamInfo: { applicationName: string; streamName: string; sessionId: string };
	private userData: object;
	private iceConnectionState: WebRtcState;

	constructor(config: UiWebRtcPlayerConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`
<div class="UiWebRtcPlayer">
  <div class="video-container">
    <video autoplay></video>
	<div class="spinner-container hidden"></div>	
  </div> 
</div>`);
		this.remoteVideo = this.$main.querySelector<HTMLElement>(':scope video') as HTMLVideoElement;
		this.remoteVideo.addEventListener("abort", () => { // this event will be triggered when stopPlaying() is invoked.
			this.remoteVideo.load(); // show poster again. We cannot do this directly in stopPlaying()...
		});
		this.remoteVideo.onloadedmetadata = this.onResize.bind(this);
		this.$videoContainer = this.$main.querySelector<HTMLElement>(':scope .video-container');
		this.$audioActivityDisplayContainer = this.$main.querySelector<HTMLElement>(':scope .audio-activity-display-container');
		this.$spinnerContainer = this.$main.querySelector<HTMLElement>(':scope .spinner-container');
		this.$spinnerContainer.append(new UiSpinner().getMainDomElement());

		this.setBackgroundImageUrl(config.backgroundImageUrl);

		if (config.playingSettings != null) {
			this.play(config.playingSettings);
		}
	}

	public static async getPlayableVideoCodecs(): Promise<UiVideoCodec[]> {
		return await getPlayableVideoCodecs();
	}

	public play(settings: UiWebRtcPlayingSettingsConfig) {
		this.stopPlaying();
		this.settings = settings;
		this.reconnectForPlaying();
	}

	private reconnectForPlaying() {
		if (!this.settings) {
			return;
		}
		this.streamInfo = {
			applicationName: this.settings.wowzaApplicationName,
			streamName: this.settings.streamName,
			sessionId: "[empty]"
		};

		this.signalingWsConnection = new WebSocket(this.settings.signalingUrl);
		this.signalingWsConnection.binaryType = 'arraybuffer';
		this.signalingWsConnection.onopen = () => {
			this.logger.debug("this.playingSignalingWsConnection.onopen");
			this.peerConnection = new RTCPeerConnection(UiWebRtcPlayer.PEER_CONNECTION_CONFIG);
			this.peerConnection.onicecandidate = this.gotIceCandidate.bind(this);
			this.peerConnection.oniceconnectionstatechange = this.onPlayingIceConnectionStateChange.bind(this);
			(this.peerConnection as any).onaddstream = this.gotRemoteStream.bind(this);
			(this.peerConnection as any).ontrack = this.gotRemoteTrack.bind(this);
			this.logger.debug("wsURL: " + this.settings.signalingUrl);
			this.sendPlayGetOffer(this.settings.wowzaApplicationName, this.settings.streamName);
		};
		this.signalingWsConnection.onmessage = (evt) => {
			this.logger.debug("this.playingSignalingWsConnection.onmessage: " + evt.data);
			const msgJSON = JSON.parse(evt.data);
			const msgStatus = Number(msgJSON['status']);

			if (msgStatus == 514) {  // repeater stream not ready
				setTimeout(() => this.sendPlayGetOffer(this.settings.wowzaApplicationName, this.settings.streamName), 1000);
			} else if (msgStatus == 200) {
				if (msgJSON.streamInfo !== undefined) {
					this.streamInfo.sessionId = msgJSON.streamInfo.sessionId;
				}

				if (msgJSON.sdp !== undefined) {
					this.logger.debug('sdp: ' + JSON.stringify(msgJSON.sdp));
					this.peerConnection.setRemoteDescription(new RTCSessionDescription(msgJSON.sdp))
						.then(() => {
							this.peerConnection.createAnswer()
								.then(this.gotDescriptionForPlaying.bind(this), this.errorHandler.bind(this));
						}, this.errorHandler.bind(this));
				}
				const iceCandidates = msgJSON['iceCandidates'];
				if (iceCandidates !== undefined) {
					for (let index in iceCandidates) {
						this.logger.debug('iceCandidates: ' + JSON.stringify(iceCandidates[index]));
						this.peerConnection.addIceCandidate(new RTCIceCandidate(iceCandidates[index]));
					}
				}
			} else {
				this.logger.error(msgJSON.statusDescription);
				setTimeout(() => this.sendPlayGetOffer(this.settings.wowzaApplicationName, this.settings.streamName), 1000);
			}

			if ('sendResponse'.localeCompare(msgJSON.command) == 0 && this.signalingWsConnection != null) {
				this.signalingWsConnection.close();
				this.signalingWsConnection = null;
			}
		};

		this.signalingWsConnection.onclose = () => {
			this.logger.debug("this.playingSignalingWsConnection.onclose");
			this.signalingWsConnection = null;
		};

		this.signalingWsConnection.onerror = (evt) => {
			this.logger.error("this.playingSignalingWsConnection.onerror: ", evt);
			this.logger.error('WebSocket connection failed: ' + this.settings.signalingUrl);
			this.signalingWsConnection = null;
			setTimeout(() => this.reconnectForPlaying(), 1000)
		};
		this.updateUi();
	}

	public stopPlaying() {
		this.settings = null;
		if (this.peerConnection != null) {
			this.peerConnection.close();
		}
		this.peerConnection = null;

		if (this.signalingWsConnection != null) {
			this.signalingWsConnection.close();
		}
		this.signalingWsConnection = null;

		this.remoteVideo.src && (this.remoteVideo.src = "");
		this.remoteVideo.srcObject = null;

		this.logger.debug("stopPlay");
	}

	private sendPlayGetOffer(wowzaApplicationName: string, streamName: string) {
		this.userData = {param1: "value1"}; // TODO delete or make use of...
		this.logger.debug("sendPlayGetOffer: " + JSON.stringify(this.streamInfo));
		if (this.signalingWsConnection != null) { // TODO make this "thread-safe"
			this.signalingWsConnection.send('{"direction":"play", "command":"getOffer", "streamInfo":' + JSON.stringify(this.streamInfo) + ', "userData":' + JSON.stringify(this.userData) + '}');
		}
	}

	private gotIceCandidate(event: RTCPeerConnectionIceEvent) {
		if (event.candidate != null) {
			this.logger.debug('gotIceCandidate: ' + JSON.stringify({'ice': event.candidate}));
		}
	}

	private onPlayingIceConnectionStateChange(event: Event) {
		let peerConnection = event.currentTarget as RTCPeerConnection;
		let state = peerConnection.iceConnectionState;
		this.iceConnectionState = state;
		this.logger.debug("playingIceConnectionState: " + state);

		if (state === "failed") {
			setTimeout(() => this.reconnectForPlaying(), 1000);
		}

		this.updateUi();
	}

	private updateUi() {
		const nonLoadingIceStates: WebRtcState[] = ['connected', 'completed', 'closed'];
		let loading: boolean = (this.signalingWsConnection != null || nonLoadingIceStates.indexOf(this.iceConnectionState) === -1);
		this.$spinnerContainer.classList.toggle("hidden", !loading);
	}

	private gotDescriptionForPlaying(description: RTCSessionDescription) {
		this.logger.debug('gotDescription');
		this.peerConnection.setLocalDescription(description)
			.then(() => {
				this.logger.debug('sendAnswer');

				this.signalingWsConnection.send('{"direction":"play", "command":"sendResponse", "streamInfo":' + JSON.stringify(this.streamInfo) + ', "sdp":' + JSON.stringify(description) + ', "userData":' + JSON.stringify(this.userData) + '}');

			}, () => {
				this.logger.debug('set description error')
			});
	}

	private gotRemoteTrack(event: any) {
		this.logger.debug('gotRemoteTrack: kind:' + event.track.kind + ' stream:' + event.streams[0]);
		this.remoteVideo.muted = false;
		try {
			this.remoteVideo.srcObject = event.streams[0];
		} catch (error) {
			this.remoteVideo.src = window.URL.createObjectURL(event.streams[0]);
		}
	}

	private gotRemoteStream(event: any) {
		// this.logger.debug('gotRemoteStream: ' + event.stream);
		// this.remoteVideo.muted = false;
		// try {
		// 	this.remoteVideo.srcObject = event.stream;
		// } catch (error) {
		// 	this.remoteVideo.src = window.URL.createObjectURL(event.stream);
		// }
	}

	private errorHandler(error: any) {
		this.logger.error(error);
	}

	public setBackgroundImageUrl(backgroundImageUrl: string) {
		this.remoteVideo.poster = backgroundImageUrl;
	}

	public onResize(): void {
		applyDisplayMode(this.$main, this.$videoContainer, UiPageDisplayMode.FIT_SIZE, {
			innerPreferredDimensions: {
				width: this.remoteVideo.videoWidth,
				height: this.remoteVideo.videoHeight
			}
		});
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

}

(window as any).RTCPeerConnection = (window as any).RTCPeerConnection || (window as any).mozRTCPeerConnection || (window as any).webkitRTCPeerConnection;
(window as any).RTCIceCandidate = (window as any).RTCIceCandidate || (window as any).mozRTCIceCandidate || (window as any).webkitRTCIceCandidate;
(window as any).RTCSessionDescription = (window as any).RTCSessionDescription || (window as any).mozRTCSessionDescription || (window as any).webkitRTCSessionDescription;

TeamAppsUiComponentRegistry.registerComponentClass("UiWebRtcPlayer", UiWebRtcPlayer);
