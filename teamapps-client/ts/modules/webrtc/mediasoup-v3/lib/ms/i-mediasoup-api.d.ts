import { TransportOptions } from 'mediasoup-client/lib/Transport';
import { ACTION } from '../config/constants';
import { ConnectTransportRequest, ConsumerData, ConsumeRequest, ConsumeResponse, ConsumerPreferredLayers, PipeTransportConnectData, PipeTransportData, ProducerData, ProduceRequest, ProduceResponse, ServerConfigs, StartRecordingRequest, StatsInput, StatsOutput, StopRecordingRequest, StreamFileRequest, TransportBitrateData, TransportData } from '../front/src/client-interfaces';
export interface IMediasoupApi extends Record<ACTION, (json: {}) => Promise<{} | void>> {
    [ACTION.RESUME_CONSUMER](json: ConsumerData): Promise<void>;
    [ACTION.PAUSE_CONSUMER](json: ConsumerData): Promise<void>;
    [ACTION.SET_PREFERRED_LAYERS](json: ConsumerPreferredLayers): Promise<void>;
    [ACTION.RESUME_PRODUCER](json: ProducerData): Promise<void>;
    [ACTION.PAUSE_PRODUCER](json: ProducerData): Promise<void>;
    [ACTION.CLOSE_PRODUCER](json: ProducerData): Promise<void>;
    [ACTION.PRODUCE](json: ProduceRequest): Promise<ProduceResponse>;
    [ACTION.CONSUME](json: ConsumeRequest): Promise<ConsumeResponse>;
    [ACTION.CREATE_PIPE_TRANSPORT](): Promise<PipeTransportData>;
    [ACTION.CONNECT_PIPE_TRANSPORT](json: PipeTransportConnectData): Promise<void>;
    [ACTION.CLOSE_TRANSPORT](json: TransportData): Promise<void>;
    [ACTION.GET_SERVER_CONFIGS](): Promise<ServerConfigs>;
    [ACTION.CREATE_TRANSPORT](): Promise<TransportOptions>;
    [ACTION.CONNECT_TRANSPORT](json: ConnectTransportRequest): Promise<void>;
    [ACTION.STREAM_FILE](json: StreamFileRequest): Promise<void>;
    [ACTION.START_RECORDING](json: StartRecordingRequest): Promise<void>;
    [ACTION.STOP_RECORDING](json: StopRecordingRequest): Promise<void>;
    [ACTION.SET_MAX_INCOMING_BITRATE](json: TransportBitrateData): Promise<void>;
    [ACTION.TRANSPORT_STATS](json: StatsInput): Promise<StatsOutput>;
    [ACTION.CONSUMERS_STATS](json: StatsInput): Promise<StatsOutput>;
    [ACTION.PRODUCERS_STATS](json: StatsInput): Promise<StatsOutput>;
}
