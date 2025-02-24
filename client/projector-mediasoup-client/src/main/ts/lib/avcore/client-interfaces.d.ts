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
import {MediaKind, RtpCapabilities, RtpEncodingParameters, RtpParameters} from 'mediasoup-client/lib/RtpParameters';
import {ProducerCodecOptions} from 'mediasoup-client/lib/Producer';
import {DtlsParameters} from 'mediasoup-client/lib/Transport';
import {MIXER_PIPE_TYPE, MIXER_RENDER_TYPE} from './constants';

export declare type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>;

export interface ConsumerData {
	consumerId: string;
}

export interface ConsumerPreferredLayers extends ConsumerData {
	layers: ConsumerLayers;
}

export interface ConsumerLayers {
	spatialLayer: number;
	temporalLayer?: number;
}

export interface ProducerData {
	producerId: string;
}

export interface ProduceRequest extends StreamKindData {
	transportId: string;
	rtpParameters: RtpParameters;
	paused?: boolean;
	keyFrameRequestDelay?: number;
	appData?: any;
}

export interface ProduceResponse {
	id: string;
}

export interface ConsumeResponse {
	producerId: string;
	id: string;
	kind: MediaKind;
	rtpParameters: RtpParameters;
	type: string;
	producerPaused: boolean;
}

export interface ConsumeRequestOriginDataServer extends ConferenceInputOrigin {
	token: string;
}

export interface ConsumeRequestOriginData {
	source: ConsumeRequestOriginDataServer;
	target: ConsumeRequestOriginDataServer;
}

export interface ConsumeRequest extends StreamKindData {
	rtpCapabilities: RtpCapabilities;
	transportId: string;
}

export interface PipeToRemoteProducerRequest extends StreamKindData {
	origin: ConsumeRequestOriginData;
	sameHost: boolean;
}

export interface PipeFromRemoteProducerRequest extends ProducerData, StreamKindData {
	workerId: number;
}

export interface PipeTransportData {
	pipeTransportId: string;
	ip: string;
	port: number;
}

export interface PipeTransportConnectData extends PipeTransportData {
	transportId: string;
}

export interface WorkerLoadData {
	currentLoad: number;
}

export interface NumWorkersData {
	num: number;
}

export interface StatsInput {
	ids: string[];
}

export interface StatsOutput {
	[x: string]: {};
}

export interface TransportData {
	transportId: string;
}

export interface TransportBitrateData extends TransportData {
	bitrate: number;
}

export interface IceSever {
	urls: string[];
	username?: string;
	credential?: string;
}

export interface Simulcast {
	encodings?: RtpEncodingParameters[];
	codecOptions?: ProducerCodecOptions;
}

export interface ServerConfigs {
	routerRtpCapabilities: RtpCapabilities;
	iceServers?: IceSever[];
	simulcast?: Simulcast;
	timeout?: {
		stats: number;
		transport: number;
		consumer: number;
	};
}

export interface ConnectTransportRequest extends TransportData {
	transportId: string;
	dtlsParameters: DtlsParameters;
}

export interface RecordingData extends StreamKindsData {
}

export interface RecordingRequest extends StreamKindsData {
	layer?: number;
	origin?: ConsumeRequestOriginData;
}

export interface KindsOptionsData extends SizeData {
	kinds: MediaKind[];
}

export interface SizeData {
	width?: number;
	height?: number;
}

export interface KindsData {
	kinds?: MediaKind[];
}

export interface StreamKindsData extends StreamData {
	kinds?: MediaKind[];
}

export interface StreamKindData extends StreamData {
	kind: MediaKind;
}

export interface StreamListenData extends StreamKindData {
	origin?: ConsumeRequestOriginData;
}

export interface StreamData {
	stream: string;
}

export interface StreamFileRequest extends StreamKindsData, KindsByFileInput, StreamingOptions, PushSimulcastInput {
	restartOnExit?: boolean;
	additionalInputOptions?: string[];
}

export interface LiveStreamRequest extends StreamKindsData, StreamingOptions, PushSimulcastInput {
	url: string;
	restartOnExit?: boolean;
}

export interface BitrateOptions {
	videoBitrate?: string;
}

export interface MixerCreateOptions extends SizeData {
	frameRate?: number;
	audioSampleRate?: number;
	audioChannels?: number;
}

export interface StreamingOptions extends BitrateOptions, MixerCreateOptions {
}

export interface KindsByFileInput {
	filePath: string;
	relativePath?: boolean;
}

export interface PushStreamInputsResponse {
	options: string[];
}

export interface PushStreamInputsRequest extends PullStreamInputsRequest, PushStreamInputsResponse {
}

export interface PushStreamOptionsResponse {
	portsData: {
		[kind in MediaKind]?: PortData;
	};
	listenIp: string;
}

export interface PortData {
	payloadType: number;
	ssrcs: number[];
	rtpPort: number;
	rtcpPort: number;
	bindRtpPort?: number;
	bindRtcpPort?: number;
}

export interface PushSimulcastInput {
	simulcast?: SizeData[];
}

export interface PushStreamOptionsRequest extends PullStreamInputsRequest, PushSimulcastInput {
	bindPorts?: boolean;
}

export interface PushStreamRequest extends StreamKindsData {
	options: string[];
	restartOnExit?: boolean;
	app?: string;
	stdIn?: string;
}

export interface TransportListenIp {
	ip: string;
	announcedIp?: string;
}

export interface PullStreamInputsRequest extends StreamKindsData {
	listenIp?: TransportListenIp | string;
	layer?: number;
}

export interface PullStreamInputsResponse {
	sdp: string;
	consumerIds: {
		[id: string]: string;
	};
}

export interface ConferenceServer {
	url: string;
	worker: number;
}

export interface ConferenceInputOrigin extends ConferenceServer {
	token?: string;
}

export interface ConferenceInput {
	stopTracks?: boolean;
	worker?: number;
	url: string;
	origin?: ConferenceInputOrigin;
	stream: string;
	token: string;
	simulcast?: boolean;
	kinds?: MediaKind[];
	maxIncomingBitrate?: number;
	keyFrameRequestDelay?: number;
}

export interface ConferenceConfigTimeout {
	stats: number;
	transport: number;
	consumer: number;
}

export interface ConferenceConfig extends ConferenceInput {
	worker: number;
	kinds: MediaKind[];
	maxIncomingBitrate: number;
	timeout: ConferenceConfigTimeout;
}

export interface ListData {
	list: string[];
}

export interface FilePathInput {
	filePath: string;
}

export interface MixerInput {
	mixerId: string;
}

export interface MixerOptions {
	x: number;
	y: number;
	width: number;
	height: number;
	z: number;
	renderType?: MIXER_RENDER_TYPE;
}

export interface MixerUpdateData extends MixerInput, StreamData {
	options: MixerOptions;
}

export interface MixerAddVideoData extends MixerUpdateData {
	kind: 'video';
}

export interface MixerAddAudioData extends MixerInput, StreamData {
	kind: 'audio';
}

export interface MixerRemoveData extends MixerInput, StreamKindData {
}

export interface MixerPipeData extends MixerInput, KindsData {
}

export interface MixerHlsFormatOptions extends SizeData {
	videoBitrate: number;
}

export interface MixerPipeLiveData extends MixerPipeData, StreamData, PushSimulcastInput {
	type: MIXER_PIPE_TYPE.LIVE;
}

export interface MixerPipeRecordingData extends MixerPipeData {
	type: MIXER_PIPE_TYPE.RECORDING;
}

export interface HlsData {
	formats?: MixerHlsFormatOptions[];
	numChunks?: number;
	chunkDuration?: number;
}

export interface MixerPipeHlsData extends MixerPipeData, HlsData {
	type: MIXER_PIPE_TYPE.HLS;
	formats: MixerHlsFormatOptions[];
}

export interface MixerPipeRtmpData extends MixerPipeData {
	type: MIXER_PIPE_TYPE.RTMP;
	url: string;
}

export interface MixerPipeInput {
	pipeId: string;
}

export interface MixerPipeStopInput extends MixerPipeInput, MixerInput {
}

export interface LiveToHlsRequest extends StreamKindsData, HlsData {
	url: string;
	restartOnExit?: boolean;
}
