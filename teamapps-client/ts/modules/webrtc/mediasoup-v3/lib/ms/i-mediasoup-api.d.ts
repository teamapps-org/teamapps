import { ConnectTransportRequest, ConsumerData, ConsumeRequest, ConsumeResponse, CreateTransportResponse, PipeTransportConnectData, PipeTransportData, ProducerData, ProduceRequest, ProduceResponse, StartRecordingRequest, StopRecordingRequest, StreamFileRequest, TransportData } from './interfaces';
import { RtpCapabilities } from 'mediasoup-client/lib/RtpParameters';
import { TransportOptions } from 'mediasoup-client/lib/Transport';
import { ACTION } from '../config/constants';
export interface IMediasoupApi extends Record<ACTION, (json: {}) => Promise<{} | void>> {
    [ACTION.RESUME_CONSUMER](json: ConsumerData): Promise<void>;
    [ACTION.PAUSE_CONSUMER](json: ConsumerData): Promise<void>;
    [ACTION.RESUME_PRODUCER](json: ProducerData): Promise<void>;
    [ACTION.PAUSE_PRODUCER](json: ProducerData): Promise<void>;
    [ACTION.CLOSE_PRODUCER](json: ProducerData): Promise<void>;
    [ACTION.PRODUCE](json: ProduceRequest): Promise<ProduceResponse>;
    [ACTION.CONSUME](json: ConsumeRequest): Promise<ConsumeResponse>;
    [ACTION.CREATE_PIPE_TRANSPORT](): Promise<PipeTransportData>;
    [ACTION.CONNECT_PIPE_TRANSPORT](json: PipeTransportConnectData): Promise<void>;
    [ACTION.CLOSE_TRANSPORT](json: TransportData): Promise<void>;
    [ACTION.GET_ROUTER_RTP_CAPABILITIES](): Promise<RtpCapabilities>;
    [ACTION.CREATE_TRANSPORT](): Promise<TransportOptions | CreateTransportResponse>;
    [ACTION.CONNECT_TRANSPORT](json: ConnectTransportRequest): Promise<void>;
    [ACTION.STREAM_FILE](json: StreamFileRequest): Promise<void>;
    [ACTION.START_RECORDING](json: StartRecordingRequest): Promise<void>;
    [ACTION.STOP_RECORDING](json: StopRecordingRequest): Promise<void>;
}
