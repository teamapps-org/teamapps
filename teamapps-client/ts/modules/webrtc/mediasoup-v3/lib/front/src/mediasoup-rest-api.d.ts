import { ConnectTransportRequest, ConsumerData, ConsumeRequest, ConsumeResponse, PipeTransportConnectData, PipeTransportData, ProducerData, ProduceRequest, ProduceResponse, StartRecordingRequest, StreamFileRequest, TransportData } from '../../ms/interfaces';
import { RtpCapabilities } from 'mediasoup-client/lib/RtpParameters';
import { TransportOptions } from 'mediasoup-client/lib/Transport';
import { IMediasoupApi } from '../../ms/i-mediasoup-api';
export declare class MediasoupRestApi implements IMediasoupApi {
    url: string;
    token: string;
    constructor(url: string, token: string);
    resumeConsumer(json: ConsumerData): Promise<void>;
    pauseConsumer(json: ConsumerData): Promise<void>;
    closeConsumer(json: ConsumerData): Promise<void>;
    resumeProducer(json: ProducerData): Promise<void>;
    pauseProducer(json: ProducerData): Promise<void>;
    closeProducer(json: ProducerData): Promise<void>;
    produce(json: ProduceRequest): Promise<ProduceResponse>;
    consume(json: ConsumeRequest): Promise<ConsumeResponse>;
    createPipeTransport(): Promise<PipeTransportData>;
    connectPipeTransport(json: PipeTransportConnectData): Promise<void>;
    closeTransport(json: TransportData): Promise<void>;
    getRouterRtpCapabilities(): Promise<RtpCapabilities>;
    createTransport(): Promise<TransportOptions>;
    connectTransport(json: ConnectTransportRequest): Promise<void>;
    startRecording(json: StartRecordingRequest): Promise<void>;
    stopRecording(json: StartRecordingRequest): Promise<void>;
    streamFile(json: StreamFileRequest): Promise<void>;
    private request;
}
