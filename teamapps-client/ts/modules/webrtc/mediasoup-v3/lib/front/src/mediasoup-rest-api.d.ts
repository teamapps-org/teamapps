import { ConnectTransportRequest, ConsumerData, ConsumeRequest, ConsumeResponse, ConsumerPreferredLayers, PipeTransportConnectData, PipeTransportData, ProducerData, ProduceRequest, ProduceResponse, ServerConfigs, StartRecordingRequest, StreamFileRequest, TransportData } from '../../ms/interfaces';
import { TransportOptions } from 'mediasoup-client/lib/Transport';
import { IMediasoupApi } from '../../ms/i-mediasoup-api';
export declare class MediasoupRestApi implements IMediasoupApi {
    private readonly url;
    private readonly token;
    private readonly timeouts;
    constructor(url: string, token: string);
    resumeConsumer(json: ConsumerData): Promise<void>;
    pauseConsumer(json: ConsumerData): Promise<void>;
    setPreferredLayers(json: ConsumerPreferredLayers): Promise<void>;
    closeConsumer(json: ConsumerData): Promise<void>;
    resumeProducer(json: ProducerData): Promise<void>;
    pauseProducer(json: ProducerData): Promise<void>;
    closeProducer(json: ProducerData): Promise<void>;
    produce(json: ProduceRequest): Promise<ProduceResponse>;
    consume(json: ConsumeRequest): Promise<ConsumeResponse>;
    createPipeTransport(): Promise<PipeTransportData>;
    connectPipeTransport(json: PipeTransportConnectData): Promise<void>;
    closeTransport(json: TransportData): Promise<void>;
    getServerConfigs(): Promise<ServerConfigs>;
    createTransport(): Promise<TransportOptions>;
    connectTransport(json: ConnectTransportRequest): Promise<void>;
    startRecording(json: StartRecordingRequest): Promise<void>;
    stopRecording(json: StartRecordingRequest): Promise<void>;
    streamFile(json: StreamFileRequest): Promise<void>;
    clear(): void;
    private request;
}
