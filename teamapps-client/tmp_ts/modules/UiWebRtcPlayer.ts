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

import {DtoWebRtcPlayerCommandHandler, DtoWebRtcPlayer} from "../generated/DtoWebRtcPlayer";
import {UiSpinner} from "./micro-components/UiSpinner";
import {AbstractComponent} from "teamapps-client-core";
import {TeamAppsUiContext} from "teamapps-client-core";
import {applyDisplayMode, parseHtml} from "./Common";
import {UiPageDisplayMode} from "../generated/UiPageDisplayMode";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {DtoWebRtcPlayingSettings} from "../generated/DtoWebRtcPlayingSettings";
import {getPlayableVideoCodecs} from "./util/getPlayableVideoCodecs";
import {UiVideoCodec} from "../generated/UiVideoCodec";

type WebRtcState = 'new' | 'checking' | 'connected' | 'completed' | 'failed' | 'disconnected' | 'closed';

export class UiWebRtcPlayer extends AbstractLegacyComponent<DtoWebRtcPlayer> implements DtoWebRtcPlayerCommandHandler {

	private static readonly PEER_CONNECTION_CONFIG: any = {'iceServers': []};

	private $main: HTMLElement;
	private $videoContainer: HTMLElement;
	private $audioActivityDisplayContainer: HTMLElement;
	private $spinnerContainer: HTMLElement;

	private remoteVideo: HTMLVideoElement;
	private peerConnection: RTCPeerConnection = null;
	private settings: DtoWebRtcPlayingSettings;
	private signalingWsConnection: WebSocket = null;
	private streamInfo: { applicationName: string; streamName: string; sessionId: string };
	private userData: object;
	private iceConnectionState: WebRtcState;

	constructor(config: DtoWebRtcPlayer, serverChannel: ServerChannel) {
		super(config, serverChannel);

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

	public play(settings: DtoWebRtcPlayingSettings) {
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
			console.debug("this.playingSignalingWsConnection.onopen");
			this.peerConnection = new RTCPeerConnection(UiWebRtcPlayer.PEER_CONNECTION_CONFIG);
			this.peerConnection.onicecandidate = this.gotIceCandidate.bind(this);
			this.peerConnection.oniceconnectionstatechange = this.onPlayingIceConnectionStateChange.bind(this);
			(this.peerConnection as any).onaddstream = this.gotRemoteStream.bind(this);
			(this.peerConnection as any).ontrack = this.gotRemoteTrack.bind(this);
			console.debug("wsURL: " + this.settings.signalingUrl);
			this.sendPlayGetOffer(this.settings.wowzaApplicationName, this.settings.streamName);
		};
		this.signalingWsConnection.onmessage = (evt) => {
			console.debug("this.playingSignalingWsConnection.onmessage: " + evt.data);
			const msgJSON = JSON.parse(evt.data);
			const msgStatus = Number(msgJSON['status']);

			if (msgStatus == 514) {  // repeater stream not ready
				setTimeout(() => this.sendPlayGetOffer(this.settings.wowzaApplicationName, this.settings.streamName), 1000);
			} else if (msgStatus == 200) {
				if (msgJSON.streamInfo !== undefined) {
					this.streamInfo.sessionId = msgJSON.streamInfo.sessionId;
				}

				if (msgJSON.sdp !== undefined) {
					console.debug('sdp: ' + JSON.stringify(msgJSON.sdp));
					this.peerConnection.setRemoteDescription(new RTCSessionDescription(msgJSON.sdp))
						.then(() => {
							this.peerConnection.createAnswer()
								.then(this.gotDescriptionForPlaying.bind(this), this.errorHandler.bind(this));
						}, this.errorHandler.bind(this));
				}
				const iceCandidates = msgJSON['iceCandidates'];
				if (iceCandidates !== undefined) {
					for (let index in iceCandidates) {
						console.debug('iceCandidates: ' + JSON.stringify(iceCandidates[index]));
						this.peerConnection.addIceCandidate(new RTCIceCandidate(iceCandidates[index]));
					}
				}
			} else {
				console.error(msgJSON.statusDescription);
				setTimeout(() => this.sendPlayGetOffer(this.settings.wowzaApplicationName, this.settings.streamName), 1000);
			}

			if ('sendResponse'.localeCompare(msgJSON.command) == 0 && this.signalingWsConnection != null) {
				this.signalingWsConnection.close();
				this.signalingWsConnection = null;
			}
		};

		this.signalingWsConnection.onclose = () => {
			console.debug("this.playingSignalingWsConnection.onclose");
			this.signalingWsConnection = null;
		};

		this.signalingWsConnection.onerror = (evt) => {
			console.error("this.playingSignalingWsConnection.onerror: ", evt);
			console.error('WebSocket connection failed: ' + this.settings.signalingUrl);
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

		console.debug("stopPlay");
	}

	private sendPlayGetOffer(wowzaApplicationName: string, streamName: string) {
		this.userData = {param1: "value1"}; // TODO delete or make use of...
		console.debug("sendPlayGetOffer: " + JSON.stringify(this.streamInfo));
		if (this.signalingWsConnection != null) { // TODO make this "thread-safe"
			this.signalingWsConnection.send('{"direction":"play", "command":"getOffer", "streamInfo":' + JSON.stringify(this.streamInfo) + ', "userData":' + JSON.stringify(this.userData) + '}');
		}
	}

	private gotIceCandidate(event: RTCPeerConnectionIceEvent) {
		if (event.candidate != null) {
			console.debug('gotIceCandidate: ' + JSON.stringify({'ice': event.candidate}));
		}
	}

	private onPlayingIceConnectionStateChange(event: Event) {
		let peerConnection = event.currentTarget as RTCPeerConnection;
		let state = peerConnection.iceConnectionState;
		this.iceConnectionState = state;
		console.debug("playingIceConnectionState: " + state);

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
		console.debug('gotDescription');
		this.peerConnection.setLocalDescription(description)
			.then(() => {
				console.debug('sendAnswer');

				this.signalingWsConnection.send('{"direction":"play", "command":"sendResponse", "streamInfo":' + JSON.stringify(this.streamInfo) + ', "sdp":' + JSON.stringify(description) + ', "userData":' + JSON.stringify(this.userData) + '}');

			}, () => {
				console.debug('set description error')
			});
	}

	private gotRemoteTrack(event: any) {
		console.debug('gotRemoteTrack: kind:' + event.track.kind + ' stream:' + event.streams[0]);
		this.remoteVideo.muted = false;
		try {
			this.remoteVideo.srcObject = event.streams[0];
		} catch (error) {
			this.remoteVideo.src = window.URL.createObjectURL(event.streams[0]);
		}
	}

	private gotRemoteStream(event: any) {
		// console.debug('gotRemoteStream: ' + event.stream);
		// this.remoteVideo.muted = false;
		// try {
		// 	this.remoteVideo.srcObject = event.stream;
		// } catch (error) {
		// 	this.remoteVideo.src = window.URL.createObjectURL(event.stream);
		// }
	}

	private errorHandler(error: any) {
		console.error(error);
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


