/*-
 * TeamApps
 * ---
 * Copyright (C) 2019 TeamApps.org
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
 */

import {Device, Producer, Transport} from "mediasoup-client";

const socketClient = require('socket.io-client');
const socketPromise = require('./socket.io-promise').promise;

type ConnectionEventType = 'disconnect' | 'newVideoProducer' | 'videoProducerGone';
type PublishStatus = 'connecting' | 'connected' | 'failed' | 'updateStream';

export class Sender {
	private socket: SocketIOClient.Socket & any;
	private device: Device;
	private publishTransport: Transport;
	private publishVideoStream: MediaStream;
	private publishAudioStream: MediaStream;
	private clientUUID: string;
	private streamUUID: string;
	private authToken: string;
	private sourceConstraints: MediaStreamConstraints;
	private webCamProducer: Producer;
	private micProducer: Producer;
	private serverDomain: string;
	private serverPort: number;

	public constructor(serverDomain: string, serverPort: number) {
		this.clientUUID = this.createUUID();
		this.sourceConstraints = {audio: true, video: true}
		this.serverDomain = serverDomain;
		this.serverPort = serverPort;
	}

	public async connect(callback: (eventType: ConnectionEventType) => void) {
		return new Promise((resolve, reject) => {
			const opts = {
				path: '/server',
				transports: ['websocket'],
			};
			const serverUrl = `https://${this.serverDomain}:${this.serverPort}`;
			this.socket = socketClient(serverUrl, opts);
			this.socket.request = socketPromise(this.socket);

			this.socket.on('connect', async () => {
				console.log("websocket connected")
				const data = await this.socket.request('getRouterRtpCapabilities');
				console.log("rcp capabilities: " + data);
				await this.loadDevice(data);
				console.log("mediasoup device loaded");
				resolve();
			});
			this.socket.on('connect_error', (error: any) => {
				console.error('could not connect to %s%s (%s)', serverUrl, opts.path, error.message);
				reject(error.message);
			});

			this.socket.on('disconnect', () => {
				callback('disconnect');
			});

			this.socket.on('newVideoProducer', () => {
				callback('newVideoProducer');
			});

			this.socket.on('videoProducerGone', () => {
				callback('videoProducerGone');
			});
		});
	}

	public async disconnect(callback: () => void) {
		this.socket.disconnect();
		this.socket = null;
		callback();
	}

	private async loadDevice(routerRtpCapabilities: RTCRtpCapabilities) {
		try {
			this.device = new Device();
		} catch (error) {
			if (error.name === 'UnsupportedError') {
				console.error('browser not supported');
			}
		}
		await this.device.load({routerRtpCapabilities});
	}

	public async checkResources(streamUUID: string, authToken: string): Promise<boolean> {
		let resourcesValid: boolean = await this.socket.request('checkPublishResources',
			{streamUUID: streamUUID, authToken: authToken, clientUUID: this.clientUUID});
		if (!resourcesValid)
			console.log("stream UUID or authToken is incorrect");
		return resourcesValid;
	}

	public async publish(streamUUID: string, authToken: string, isButtonClicked: boolean, isAudioOnly: boolean,
	                     publishCallback: (status: PublishStatus, audioStream: MediaStream, videoStream: MediaStream) => void) {
		this.streamUUID = streamUUID;
		this.authToken = authToken;
		if ((this.publishTransport == undefined || this.publishTransport == null) && isButtonClicked) {
			const data = await this.socket.request('createProducerTransport', {
				forceTcp: false,
				rtpCapabilities: this.device.rtpCapabilities,
				streamUUID: streamUUID,
				authToken: authToken,
				clientUUID: this.clientUUID
			});
			if (data.error) {
				console.error(data.error);
				return;
			}

			this.publishTransport = this.device.createSendTransport(data);
			this.publishTransport.on('connect', async ({dtlsParameters}: { dtlsParameters: RTCDtlsParameters }, callback: Function, errback: Function) => {
				this.socket.request('connectProducerTransport', {
					dtlsParameters: dtlsParameters,
					streamUUID: streamUUID,
					authToken: authToken,
					clientUUID: this.clientUUID
				})
					.then(callback)
					.catch(errback);
			});

			this.publishTransport.on('produce', async ({kind, rtpParameters}: { kind: any, rtpParameters: RTCRtpParameters }, callback: Function, errback: Function) => {
				try {
					const {id} = await this.socket.request('produce', {
						transportId: this.publishTransport.id,
						kind: kind,
						rtpParameters: rtpParameters,
						streamUUID: streamUUID,
						authToken: authToken,
						clientUUID: this.clientUUID
					});
					callback({id});
				} catch (err) {
					errback(err);
				}
			});

			this.publishTransport.on('connectionstatechange', (state: string) => {
				switch (state) {
					case 'connecting':
						publishCallback('connecting', null, null);
						break;

					case 'connected':
						publishCallback('connected', null, null);
						break;

					case 'failed':
						this.publishTransport.close();
						publishCallback('failed', null, null);
						break;

					default:
						break;
				}
			});
			this.publishAudioStream = await this.getAudioUserMedia(this.publishTransport);
		}
		if (this.publishTransport != undefined && this.publishTransport != null) {
			try {
				if (!isAudioOnly) {
					this.publishVideoStream = await this.getVideoUserMedia(this.publishTransport, true);
				} else {
					if (this.publishVideoStream != undefined && this.publishVideoStream != null)
						this.closeWebCam();
					this.publishVideoStream = null;
				}
				publishCallback('updateStream', this.publishAudioStream, this.publishVideoStream);
			} catch (err) {
				console.error('get user media failed');
			}
		}
	}

	public async stopPublish() {
		const checkData = await this.socket.request('removeProducer',
			{streamUUID: this.streamUUID, authToken: this.authToken, clientUUID: this.clientUUID});
		this.removeData(checkData);
		this.publishTransport.close();
		this.publishTransport = null;
		this.publishVideoStream = null;
		this.publishAudioStream = null;
		this.streamUUID = null;
		this.authToken = null;
	}

	private async closeWebCam() {
		if (this.webCamProducer != undefined && this.webCamProducer != null) {
			this.webCamProducer.close();
			this.webCamProducer = null;
			await this.socket.request('closeVideoProducer',
				{streamUUID: this.streamUUID, authToken: this.authToken, clientUUID: this.clientUUID});
		}
	}

	private removeData(isDataRemoved: any) {
		console.log("Delete producer");
	}

	private async getVideoUserMedia(transport: Transport, isWebcam: boolean): Promise<MediaStream> {
		if (!this.device.canProduce('video')) {
			console.error('cannot produce video');
			return;
		}

		let stream;
		try {
			stream = isWebcam ?
				await navigator.mediaDevices.getUserMedia({video: true}) :
				await (<any>navigator.mediaDevices).getDisplayMedia({video: true, audio: false});
		} catch (err) {
			console.error('starting webcam failed,', err.message);
			throw err;
		}
		const track = stream.getVideoTracks()[0];
		const params = {track};
		this.webCamProducer = await transport.produce(params);
		return stream;
	}

	private async getAudioUserMedia(transport: Transport): Promise<MediaStream> {
		let stream = await navigator.mediaDevices.getUserMedia({audio: true});
		const track = stream.getAudioTracks()[0];
		this.micProducer = await transport.produce({track});
		return stream;
	}

	private createUUID(): string {
		return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
			var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
			return v.toString(16);
		});
	}
}
