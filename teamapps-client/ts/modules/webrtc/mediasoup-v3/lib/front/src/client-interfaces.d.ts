import { MediaKind, RtpCapabilities, RtpParameters } from 'mediasoup-client/lib/RtpParameters';
import { ProducerCodecOptions } from 'mediasoup-client/lib/Producer';
import { DtlsParameters } from 'mediasoup-client/lib/Transport';
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
export interface ProduceRequest {
    transportId: string;
    stream: string;
    kind: MediaKind;
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
export interface ConsumeRequestOriginData {
    token: string;
    from: string;
    to: string;
}
export interface ConsumeRequest {
    origin?: ConsumeRequestOriginData;
    kind: MediaKind;
    stream: string;
    rtpCapabilities: RtpCapabilities;
    transportId: string;
}
export interface PipeToRemoteProducerRequest {
    origin: ConsumeRequestOriginData;
    kind: MediaKind;
    stream: string;
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
    encodings?: RTCRtpEncodingParameters[];
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
export interface StartRecordingRequest extends StopRecordingRequest {
    wait?: boolean;
    filePath?: string;
}
export interface StopRecordingRequest {
    stream: string;
    kinds?: MediaKind[];
}
export interface StopRecordingData extends StopRecordingRequest {
    filePath: string;
}
export interface StreamFileRequest extends StopRecordingRequest {
    filePath: string;
}
export interface ConferenceInput {
    url?: string;
    originUrl?: string;
    stream: string;
    token: string;
    simulcast?: boolean;
    kinds?: MediaKind[];
    maxIncomingBitrate?: number;
}
export interface ConferenceConfig extends ConferenceInput {
    url: string;
    kinds: MediaKind[];
    maxIncomingBitrate: number;
    timeout: {
        stats: number;
        transport: number;
        consumer: number;
    };
}
