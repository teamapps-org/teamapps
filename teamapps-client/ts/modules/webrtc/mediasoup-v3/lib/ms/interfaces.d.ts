import { WorkerSettings } from 'mediasoup/lib/Worker';
import { RouterOptions } from 'mediasoup/lib/Router';
import { ConsumerLayers, ConsumerType } from 'mediasoup/lib/Consumer';
import { MediaKind, RtpCapabilities, RtpCodecParameters, RtpParameters } from 'mediasoup/lib/RtpParameters';
import { DtlsParameters, IceCandidate, IceParameters } from 'mediasoup/lib/WebRtcTransport';
import { RtpCapabilities as ClientRtpCapabilities } from 'mediasoup-client/lib/RtpParameters';
import { ProducerCodecOptions } from 'mediasoup-client/lib/Producer';
export interface MediaSoupSettings {
    worker: WorkerSettings;
    router: RouterOptions;
    webRtcTransport: {
        listenIp: string;
        maxIncomingBitrate: number;
        initialAvailableOutgoingBitrate: number;
    };
    codecParameters: {
        [x in MediaKind]: RtpCodecParameters;
    };
    sdp: {
        audio: string;
        video: string;
        header: string;
        minPort: number;
    };
    recording: {
        path: string;
        extension: string;
    };
    ffmpeg: {
        path: string;
        encoding: {
            [x in MediaKind]: string[];
        };
    };
    timeout: {
        stats: number;
        stream: number;
    };
    iceServers?: IceSever[];
    simulcast?: Simulcast;
}
export interface IceSever {
    urls: string[];
    username: string;
    credential: string;
}
export interface Simulcast {
    encodings?: RTCRtpEncodingParameters[];
    codecOptions?: ProducerCodecOptions;
}
export interface ServerConfigs {
    routerRtpCapabilities: RtpCapabilities;
    iceServers?: IceSever[];
    simulcast?: Simulcast;
}
export interface ProduceRequest {
    transportId: string;
    stream: string;
    kind: MediaKind;
    rtpParameters: RtpParameters;
    paused?: boolean;
    keyFrameRequestDelay?: number;
}
export interface ProduceResponse {
    id: string;
}
export interface ConsumeResponse {
    producerId: string;
    id: string;
    kind: MediaKind;
    rtpParameters: RtpParameters;
    type: ConsumerType;
    producerPaused: boolean;
}
export interface ConsumeRequest {
    kind: MediaKind;
    stream: string;
    rtpCapabilities: RtpCapabilities | ClientRtpCapabilities;
    transportId: string;
}
export interface CreateTransportResponse {
    id: string;
    iceParameters: IceParameters;
    iceCandidates: IceCandidate[];
    dtlsParameters: DtlsParameters;
}
export interface ConnectTransportRequest {
    transportId: string;
    dtlsParameters: DtlsParameters;
}
export interface StopRecordingRequest {
    stream: string;
    kinds?: MediaKind[];
}
export interface StartRecordingRequest extends StopRecordingRequest {
    wait?: boolean;
    filePath?: string;
}
export interface StopRecordingData extends StopRecordingRequest {
    filePath: string;
}
export interface StartRestreamingRequest {
    stream: string;
    targetUrl: string;
    kinds?: MediaKind[];
    token: string;
}
export interface ProducerData {
    producerId: string;
}
export interface ConsumerData {
    consumerId: string;
}
export interface ConsumerPreferredLayers extends ConsumerData {
    layers: ConsumerLayers;
}
export interface TransportData {
    transportId: string;
}
export interface StreamFileRequest extends StopRecordingRequest {
    filePath: string;
}
export interface PipeTransportData {
    pipeTransportId: string;
    ip: string;
    port: number;
}
export interface PipeTransportConnectData extends PipeTransportData {
    transportId: string;
}
export interface ConferenceInput {
    url?: string;
    stream: string;
    token: string;
    simulcast?: boolean;
    kinds?: MediaKind[];
    timeout?: {
        stats: number;
        stream: number;
    };
}
export interface ConferenceConfig extends ConferenceInput {
    url: string;
    kinds: MediaKind[];
    timeout: {
        stats: number;
        stream: number;
    };
}
