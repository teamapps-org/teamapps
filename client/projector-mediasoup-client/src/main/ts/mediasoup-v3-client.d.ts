/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
declare module 'mediasoup-client' {

	export class CommandQueue {
		constructor();

		close(): void;

		/**
		 * @param {Function} command - Function that returns a promise.
		 *
		 * @async
		 */
		push(command: () => void): /* CommandQueue.prototype.+Promise */ Promise<void>;
	}

	export class Consumer {
		/**
		 * @private
		 *
		 * @emits transportclose
		 * @emits trackended
		 * @emits @getstats
		 * @emits @close
		 */
		constructor({
			    id,
			    localId,
			    producerId,
			    track,
			    rtpParameters,
			    appData,
		    }: {
			id: string;
			localId: string;
			producerId: string;
			track: MediaStreamTrack;
			rtpParameters: RTCRtpParameters;
			appData?: object;
		});

		/**
		 * Consumer id.
		 *
		 * @returns {String}
		 */
		id: string;

		/**
		 * Local id.
		 *
		 * @private
		 * @returns {String}
		 */
		localId: string;

		/**
		 * Associated Producer id.
		 *
		 * @returns {String}
		 */
		producerId: string;

		/**
		 * Whether the Consumer is closed.
		 *
		 * @returns {Boolean}
		 */
		closed: boolean;

		/**
		 * Media kind.
		 *
		 * @returns {String}
		 */
		kind: 'audio' | 'video';

		/**
		 * The associated track.
		 *
		 * @returns {MediaStreamTrack}
		 */
		track: MediaStreamTrack;

		/**
		 * RTP parameters.
		 *
		 * @returns {RTCRtpParameters}
		 */
		rtpParameters: RTCRtpParameters;

		/**
		 * Whether the Consumer is paused.
		 *
		 * @returns {Boolean}
		 */
		paused: boolean;

		/**
		 * App custom data.
		 *
		 * @returns {Object}
		 */
		appData: object;

		on(type: any, listener: (...params: any) => Promise<void> | void): Promise<void> | void;

		/**
		 * Closes the Consumer.
		 */
		close(): void;

		// /**
		//  * Transport was closed.
		//  *
		//  * @private
		//  */
		// transportClosed(): void;

		/**
		 * Get associated RTCRtpReceiver stats.
		 *
		 * @async
		 * @returns {RTCStatsReport}
		 * @throws {InvalidStateError} if Consumer closed.
		 * @return
		 */
		getStats(): Promise<RTCStatsReport>;

		/**
		 * Pauses receiving media.
		 */
		pause(): void;

		/**
		 * Resumes receiving media.
		 */
		resume(): void;
	}

	export class DataConsumer {
		/**
		 * @private
		 *
		 * @emits transportclose
		 * @emits open
		 * @emits {Object} error
		 * @emits close
		 * @emits {Any} message
		 * @emits @close
		 */
		constructor({
			    id,
			    dataProducerId,
			    dataChannel,
			    sctpStreamParameters,
			    appData,
		    }: {
			id: string;
			dataProducerId: string;
			dataChannel: RTCDataChannel;
			sctpStreamParameters: any;
			appData: object;
		});

		/**
		 * DataConsumer id.
		 *
		 * @returns {String}
		 */
		id: string;

		/**
		 * Associated DataProducer id.
		 *
		 * @returns {String}
		 */
		dataProducerId: string;

		/**
		 * Whether the DataConsumer is closed.
		 *
		 * @returns {Boolean}
		 */
		closed: boolean;

		/**
		 * SCTP stream parameters.
		 *
		 * @returns {RTCSctpStreamParameters}
		 */
		sctpStreamParameters: any;

		/**
		 * DataChannel readyState.
		 *
		 * @returns {String}
		 */
		readyState: string;

		/**
		 * DataChannel label.
		 *
		 * @returns {String}
		 */
		label: string;

		/**
		 * DataChannel protocol.
		 *
		 * @returns {String}
		 */
		protocol: string;

		/**
		 * DataChannel binaryType.
		 *
		 * @returns {String}
		 */
		binaryType: string;

		/**
		 * App custom data.
		 *
		 * @returns {Object}
		 */
		appData: object;

		/**
		 * Closes the DataConsumer.
		 */
		close(): void;

		// /**
		//  * Transport was closed.
		//  *
		//  * @private
		//  */
		// transportClosed(): void;
	}

	export class DataProducer {
		/**
		 * @private
		 *
		 * @emits transportclose
		 * @emits open
		 * @emits {Object} error
		 * @emits close
		 * @emits bufferedamountlow
		 * @emits @close
		 */
		constructor({
			    id,
			    dataChannel,
			    sctpStreamParameters,
			    appData,
		    }: {
			id: string;
			dataChannel: RTCDataChannel;
			sctpStreamParameters: any;
			appData: object;
		});

		/**
		 * DataProducer id.
		 *
		 * @returns {String}
		 */
		id: string;

		/**
		 * Whether the DataProducer is closed.
		 *
		 * @returns {Boolean}
		 */
		closed: boolean;

		/**
		 * SCTP stream parameters.
		 *
		 * @returns {RTCSctpStreamParameters}
		 */
		sctpStreamParameters: any;

		/**
		 * DataChannel readyState.
		 *
		 * @returns {String}
		 */
		readyState: string;

		/**
		 * DataChannel label.
		 *
		 * @returns {String}
		 */
		label: string;

		/**
		 * DataChannel protocol.
		 *
		 * @returns {String}
		 */
		protocol: string;

		/**
		 * DataChannel bufferedAmount.
		 *
		 * @returns {String}
		 */
		bufferedAmount: string;

		/**
		 * DataChannel bufferedAmountLowThreshold.
		 *
		 * @returns {String}
		 */
		bufferedAmountLowThreshold: string;

		/**
		 * App custom data.
		 *
		 * @returns {Object}
		 */
		appData: object;

		/**
		 * Closes the DataProducer.
		 */
		close(): void;

		/**
		 * Send a message.
		 *
		 * @param {String|Blob|ArrayBuffer|ArrayBufferView} data.
		 *
		 * @throws {InvalidStateError} if DataProducer closed.
		 * @throws {TypeError} if wrong arguments.
		 * @param data
		 */
		send(data: string | Blob | ArrayBuffer | ArrayBufferView): void;

		// /**
		//  * Transport was closed.
		//  *
		//  * @private
		//  */
		// transportClosed(): void;
	}

	export class Device {
		/**
		 * Create a new Device to connect to mediasoup server.
		 *
		 * @param {Class|String} [Handler] - An optional RTC handler class for unsupported or
		 *   custom devices (not needed when running in a browser). If a String, it will
		 *   force usage of the given built-in handler.
		 *
		 * @throws {UnsupportedError} if device is not supported.
		 */
		constructor({Handler}?: { Handler?: any });

		/**
		 * Whether the Device is loaded.
		 *
		 * @returns {Boolean}
		 */
		loaded: boolean;

		/**
		 * The RTC handler class name ('Chrome70', 'Firefox65', etc).
		 *
		 * @returns {RTCRtpCapabilities}
		 */
		handlerName: RTCRtpCapabilities;

		/**
		 * RTP capabilities of the Device for receiving media.
		 *
		 * @returns {RTCRtpCapabilities}
		 * @throws {InvalidStateError} if not loaded.
		 */
		rtpCapabilities: string;

		/**
		 * SCTP capabilities of the Device.
		 *
		 * @returns {Object}
		 * @throws {InvalidStateError} if not loaded.
		 */
		sctpCapabilities: object;

		/**
		 * Initialize the Device.
		 *
		 * @param {RTCRtpCapabilities} routerRtpCapabilities - Router RTP capabilities.
		 *
		 * @async
		 * @throws {TypeError} if missing/wrong arguments.
		 * @throws {InvalidStateError} if already loaded.
		 * @param {routerRtpCapabilities}
		 */
		load({routerRtpCapabilities}: { routerRtpCapabilities: RTCRtpCapabilities }): Promise<void>;

		/**
		 * Whether we can produce audio/video.
		 *
		 * @param {String} kind - 'audio' or 'video'.
		 *
		 * @returns {Boolean}
		 * @throws {InvalidStateError} if not loaded.
		 * @throws {TypeError} if wrong arguments.
		 */
		canProduce(kind: string): boolean;

		/**
		 * Creates a Transport for sending media.
		 *
		 * @param {String} - Server-side Transport id.
		 * @param {RTCIceParameters} iceParameters - Server-side Transport ICE parameters.
		 * @param {Array<RTCIceCandidate>} [iceCandidates] - Server-side Transport ICE candidates.
		 * @param {RTCDtlsParameters} dtlsParameters - Server-side Transport DTLS parameters.
		 * @param {Object} [sctpParameters] - Server-side SCTP parameters.
		 * @param {Array<RTCIceServer>} [iceServers] - Array of ICE servers.
		 * @param {RTCIceTransportPolicy} [iceTransportPolicy] - ICE transport
		 *   policy.
		 * @param {Object} [proprietaryConstraints] - RTCPeerConnection proprietary constraints.
		 * @param {Object} [appData={}] - Custom app data.
		 *
		 * @returns {Transport}
		 * @throws {InvalidStateError} if not loaded.
		 * @throws {TypeError} if wrong arguments.
		 */
		createSendTransport({
			                    id,
			                    iceParameters,
			                    iceCandidates,
			                    dtlsParameters,
			                    sctpParameters,
			                    iceServers,
			                    iceTransportPolicy,
			                    proprietaryConstraints,
			                    appData,
		                    }: {
			id: string;
			iceParameters: RTCIceParameters;
			iceCandidates: RTCIceCandidate[];
			dtlsParameters: RTCDtlsParameters;
			sctpParameters?: object;
			iceServers?: RTCIceServer[];
			iceTransportPolicy?: RTCIceTransportPolicy;
			proprietaryConstraints?: object;
			appData?: object;
		}): Transport;

		/**
		 * Creates a Transport for receiving media.
		 *
		 * @param {String} - Server-side Transport id.
		 * @param {RTCIceParameters} iceParameters - Server-side Transport ICE parameters.
		 * @param {Array<RTCIceCandidate>} [iceCandidates] - Server-side Transport ICE candidates.
		 * @param {RTCDtlsParameters} dtlsParameters - Server-side Transport DTLS parameters.
		 * @param {Object} [sctpParameters] - Server-side SCTP parameters.
		 * @param {Array<RTCIceServer>} [iceServers] - Array of ICE servers.
		 * @param {RTCIceTransportPolicy} [iceTransportPolicy] - ICE transport
		 *   policy.
		 * @param {Object} [proprietaryConstraints] - RTCPeerConnection proprietary constraints.
		 * @param {Object} [appData={}] - Custom app data.
		 *
		 * @returns {Transport}
		 * @throws {InvalidStateError} if not loaded.
		 * @throws {TypeError} if wrong arguments.
		 */
		createRecvTransport({
			                    id,
			                    iceParameters,
			                    iceCandidates,
			                    dtlsParameters,
			                    sctpParameters,
			                    iceServers,
			                    iceTransportPolicy,
			                    proprietaryConstraints,
			                    appData,
		                    }: {
			id: string;
			iceParameters: RTCIceParameters;
			iceCandidates: RTCIceCandidate[];
			dtlsParameters: RTCDtlsParameters;
			sctpParameters?: object;
			iceServers?: RTCIceServer[];
			iceTransportPolicy?: RTCIceTransportPolicy;
			proprietaryConstraints?: object;
			appData?: object;
		}): Transport;
	}

	export class EnhancedEventEmitter {
		/**
		 *
		 * @param logger
		 */
		constructor(logger: any);

		/**
		 *
		 * @param event
		 * @param ...args
		 */
		safeEmit(event: any, ...args: any): void;

		/**
		 *
		 * @param event
		 * @param ...args
		 * @return
		 */
		safeEmitAsPromise(event: any, ...args: any): Promise<void>;
	}

	export class Producer {
		/**
		 * @private
		 *
		 * @emits transportclose
		 * @emits trackended
		 * @emits {track: MediaStreamTrack} @replacetrack
		 * @emits {spatialLayer: String} @setmaxspatiallayer
		 * @emits @getstats
		 * @emits @close
		 */
		constructor({
			    id,
			    localId,
			    track,
			    rtpParameters,
			    appData,
		    }: {
			id: string;
			localId: string;
			track: MediaStreamTrack;
			rtpParameters: RTCRtpParameters;
			appData?: object;
		});

		/**
		 * Producer id.
		 *
		 * @returns {String}
		 */
		id: string;

		/**
		 * Local id.
		 *
		 * @private
		 * @returns {String}
		 */
		localId: string;

		/**
		 * Whether the Producer is closed.
		 *
		 * @returns {Boolean}
		 */
		closed: boolean;

		/**
		 * Media kind.
		 *
		 * @returns {String}
		 */
		kind: 'audio' | 'video';

		/**
		 * The associated track.
		 *
		 * @returns {MediaStreamTrack}
		 */
		track: MediaStreamTrack;

		/**
		 * RTP parameters.
		 *
		 * @returns {RTCRtpParameters}
		 */
		rtpParameters: RTCRtpParameters;

		/**
		 * Whether the Producer is paused.
		 *
		 * @returns {Boolean}
		 */
		paused: boolean;

		/**
		 * Max spatial layer.
		 *
		 * @type {Number}
		 */
		maxSpatialLayer: number;

		/**
		 * App custom data.
		 *
		 * @type {Object}
		 */
		appData: object;

		on(type: any, listener: (...params: any) => Promise<void> | void): Promise<void> | void;

		/**
		 * Closes the Producer.
		 */
		close(): void;

		// /**
		//  * Transport was closed.
		//  *
		//  * @private
		//  */
		// transportClosed();

		/**
		 * Get associated RTCRtpSender stats.
		 *
		 * @promise
		 * @returns {RTCStatsReport}
		 * @throws {InvalidStateError} if Producer closed.
		 */
		getStats(): Promise<RTCStatsReport>;

		/**
		 * Pauses sending media.
		 */
		pause(): void;

		/**
		 * Resumes sending media.
		 */
		resume(): void;

		/**
		 * Replaces the current track with a new one.
		 *
		 * @param {MediaStreamTrack} track - New track.
		 *
		 * @async
		 * @throws {InvalidStateError} if Producer closed or track ended.
		 * @throws {TypeError} if wrong arguments.
		 */
		replaceTrack({track}: { track: MediaStreamTrack }): Promise<void>;

		/**
		 * Sets the video max spatial layer to be sent.
		 *
		 * @param {Number} spatialLayer
		 *
		 * @async
		 * @throws {InvalidStateError} if Producer closed.
		 * @throws {UnsupportedError} if not a video Producer.
		 * @throws {TypeError} if wrong arguments.
		 */
		setMaxSpatialLayer(spatialLayer: number): Promise<void>;
	}

	export class Transport {
		/**
		 * @private
		 *
		 * @emits {transportLocalParameters: Object, callback: Function, errback: Function} connect
		 * @emits {connectionState: String} connectionstatechange
		 * @emits {producerLocalParameters: Object, callback: Function, errback: Function} produce
		 * @emits {dataProducerLocalParameters: Object, callback: Function, errback: Function} producedata
		 */
		constructor({
			    direction,
			    id,
			    iceParameters,
			    iceCandidates,
			    dtlsParameters,
			    sctpParameters,
			    iceServers,
			    iceTransportPolicy,
			    proprietaryConstraints,
			    appData,
			    Handler,
			    extendedRtpCapabilities,
			    canProduceByKind,
		    }: {
			direction: string;
			id: string;
			iceParameters: RTCIceParameters;
			iceCandidates: RTCIceCandidate[];
			dtlsParameters: RTCDtlsParameters;
			sctpParameters: any;
			iceServers: RTCIceServer[];
			iceTransportPolicy: RTCIceTransportPolicy;
			proprietaryConstraints: object;
			appData?: object;
			Handler: any;
			extendedRtpCapabilities: object;
			canProduceByKind: object;
		});

		/**
		 * Transport id.
		 *
		 * @returns {String}
		 */
		id: string;

		/**
		 * Whether the Transport is closed.
		 *
		 * @returns {Boolean}
		 */
		closed: boolean;

		/**
		 * Transport direction.
		 *
		 * @returns {String}
		 */
		direction: string;

		/**
		 * RTC handler instance.
		 *
		 * @returns {Handler}
		 */
		handler: any;

		/**
		 * Connection state.
		 *
		 * @returns {String}
		 */
		connectionState: string;

		/**
		 * App custom data.
		 *
		 * @returns {Object}
		 */
		appData: object;

		on(type: any, listener: (...params: any) => Promise<void> | void): Promise<void> | void;

		/**
		 * Close the Transport.
		 */
		close(): void;

		/**
		 * Get associated Transport (RTCPeerConnection) stats.
		 *
		 * @async
		 * @returns {RTCStatsReport}
		 * @throws {InvalidStateError} if Transport closed.
		 */
		getStats(): Promise<RTCStatsReport>;

		/**
		 * Restart ICE connection.
		 *
		 * @param {RTCIceParameters} iceParameters - New Server-side Transport ICE parameters.
		 *
		 * @async
		 * @throws {InvalidStateError} if Transport closed.
		 * @throws {TypeError} if wrong arguments.
		 */
		restartIce({iceParameters}: { iceParameters: RTCIceParameters }): Promise<void>;

		/**
		 * Update ICE servers.
		 *
		 * @param {Array<RTCIceServer>} [iceServers] - Array of ICE servers.
		 *
		 * @async
		 * @throws {InvalidStateError} if Transport closed.
		 * @throws {TypeError} if wrong arguments.
		 */
		updateIceServers({iceServers}: { iceServers: RTCIceServer[] }): Promise<void>;

		/**
		 * Create a Producer.
		 *
		 * @param {MediaStreamTrack} track - Track to sent.
		 * @param {Array<RTCRtpCodingParameters>} [encodings] - Encodings.
		 * @param {Object} [codecOptions] - Codec options.
		 * @param {Object} [appData={}] - Custom app data.
		 *
		 * @async
		 * @returns {Producer}
		 * @throws {InvalidStateError} if Transport closed or track ended.
		 * @throws {TypeError} if wrong arguments.
		 * @throws {UnsupportedError} if Transport direction is incompatible or
		 *   cannot produce the given media kind.
		 */
		produce({
			        track,
			        encodings,
			        codecOptions,
			        appData,
		        }: {
			track: MediaStreamTrack;
			encodings?: RTCRtpCodingParameters[];
			codecOptions?: object;
			appData?: object;
		}): Promise<Producer>;

		/**
		 * Create a Consumer to consume a remote Producer.
		 *
		 * @param {String} id - Server-side Consumer id.
		 * @param {String} producerId - Server-side Producer id.
		 * @param {String} kind - 'audio' or 'video'.
		 * @param {RTCRtpParameters} rtpParameters - Server-side Consumer RTP parameters.
		 * @param {Object} [appData={}] - Custom app data.
		 *
		 * @async
		 * @returns {Consumer}
		 * @throws {InvalidStateError} if Transport closed.
		 * @throws {TypeError} if wrong arguments.
		 * @throws {UnsupportedError} if Transport direction is incompatible.
		 */
		consume({
			        id,
			        producerId,
			        kind,
			        rtpParameters,
			        appData,
		        }: {
			id: string;
			producerId: string;
			kind: string;
			rtpParameters: RTCRtpParameters;
			appData?: object;
		}): Promise<Consumer>;

		/**
		 * Create a DataProducer
		 *
		 * @param {Boolean} [ordered=true]
		 * @param {Number} [maxPacketLifeTime]
		 * @param {Number} [maxRetransmits]
		 * @param {String} [priority='low'] // 'very-low' / 'low' / 'medium' / 'high'
		 * @param {String} [label='']
		 * @param {String} [protocol='']
		 * @param {Object} [appData={}] - Custom app data.
		 *
		 * @async
		 * @returns {DataProducer}
		 * @throws {InvalidStateError} if Transport closed.
		 * @throws {TypeError} if wrong arguments.
		 * @throws {UnsupportedError} if Transport direction is incompatible or remote
		 *   transport does not enable SCTP.
		 */
		produceData({
			            ordered,
			            maxPacketLifeTime,
			            maxRetransmits,
			            priority,
			            label,
			            protocol,
			            appData,
		            }: {
			ordered?: boolean;
			maxPacketLifeTime: number;
			maxRetransmits: number;
			priority?: 'very-low' | 'low' | 'medium' | 'high';
			label?: string;
			protocol?: string;
			appData?: object;
		}): Promise<DataProducer>;

		/**
		 * Create a DataConsumer
		 *
		 * @param {String} id - Server-side DataConsumer id.
		 * @param {String} dataProducerId - Server-side DataProducer id.
		 * @param {RTCSctpStreamParameters} sctpStreamParameters - Server-side DataConsumer
		 *   SCTP parameters.
		 * @param {String} [label='']
		 * @param {String} [protocol='']
		 * @param {Object} [appData={}] - Custom app data.
		 *
		 * @async
		 * @returns {DataConsumer}
		 * @throws {InvalidStateError} if Transport closed.
		 * @throws {TypeError} if wrong arguments.
		 * @throws {UnsupportedError} if Transport direction is incompatible or remote
		 *   transport does not enable SCTP.
		 */
		consumeData({
			            id,
			            dataProducerId,
			            sctpStreamParameters,
			            label,
			            protocol,
			            appData,
		            }: {
			id: string;
			dataProducerId: string;
			sctpStreamParameters: any;
			label?: string;
			protocol?: string;
			appData?: object;
		}): Promise<DataConsumer>;

		/**
		 *
		 */
		_handleHandler(): void;

		/**
		 *
		 * @param producer
		 */
		_handleProducer(producer: Producer): void;

		/**
		 *
		 * @param consumer
		 */
		_handleConsumer(consumer: Consumer): void;

		/**
		 *
		 * @param dataProducer
		 */
		_handleDataProducer(dataProducer: DataProducer): void;

		/**
		 *
		 * @param dataConsumer
		 */
		_handleDataConsumer(dataConsumer: DataConsumer): void;
	}


	export const version: string;

	export function parseScalabilityMode(scalabilityMode: any): any;
}
