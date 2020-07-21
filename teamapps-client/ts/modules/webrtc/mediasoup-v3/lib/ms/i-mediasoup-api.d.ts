/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import { TransportOptions } from 'mediasoup-client/lib/Transport';
import { ACTION } from '../config/constants';
import { ConnectTransportRequest, ConsumerData, ConsumeRequest, ConsumeResponse, ConsumerPreferredLayers, NumWorkersData, PipeFromRemoteProducerRequest, PipeToRemoteProducerRequest, PipeTransportConnectData, PipeTransportData, ProducerData, ProduceRequest, ProduceResponse, ServerConfigs, RecordingData, StatsInput, StatsOutput, StreamFileRequest, TransportBitrateData, TransportData, WorkerLoadData, ListData, StreamData, FilePathInput, PullStreamInputsRequest, PushStreamInputsRequest, PullStreamInputsResponse, PushStreamInputsResponse, RecordingRequest, StreamKindsData, KindsByFileInput, KindsData } from '../front/src/client-interfaces';
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
    [ACTION.FILE_STREAMING](json: StreamFileRequest): Promise<void>;
    [ACTION.STOP_FILE_STREAMING](json: StreamKindsData): Promise<void>;
    [ACTION.START_RECORDING](json: RecordingRequest): Promise<void>;
    [ACTION.STOP_RECORDING](json: RecordingData): Promise<void>;
    [ACTION.SET_MAX_INCOMING_BITRATE](json: TransportBitrateData): Promise<void>;
    [ACTION.TRANSPORT_STATS](json: StatsInput): Promise<StatsOutput>;
    [ACTION.CONSUMERS_STATS](json: StatsInput): Promise<StatsOutput>;
    [ACTION.PRODUCERS_STATS](json: StatsInput): Promise<StatsOutput>;
    [ACTION.PIPE_TO_REMOTE_PRODUCER](json: PipeToRemoteProducerRequest): Promise<void>;
    [ACTION.PIPE_FROM_REMOTE_PRODUCER](json: PipeFromRemoteProducerRequest): Promise<void>;
    [ACTION.WORKER_LOAD](): Promise<WorkerLoadData>;
    [ACTION.NUM_WORKERS](): Promise<NumWorkersData>;
    [ACTION.RECORDED_STREAMS](): Promise<ListData>;
    [ACTION.STREAM_RECORDINGS](json: StreamData): Promise<ListData>;
    [ACTION.DELETE_STREAM_RECORDINGS](json: StreamData): Promise<void>;
    [ACTION.DELETE_RECORDING](json: FilePathInput): Promise<void>;
    [ACTION.PUSH_TO_SERVER_INPUTS](json: PushStreamInputsRequest): Promise<PushStreamInputsResponse>;
    [ACTION.PULL_FROM_SERVER_INPUTS](json: PullStreamInputsRequest): Promise<PullStreamInputsResponse>;
    [ACTION.KINDS_BY_FILE](json: KindsByFileInput): Promise<KindsData>;
}
