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
import { ConnectTransportRequest, ConsumerData, ConsumeRequest, ConsumeResponse, ConsumerPreferredLayers, NumWorkersData, PipeFromRemoteProducerRequest, PipeToRemoteProducerRequest, PipeTransportConnectData, PipeTransportData, ProducerData, ProduceRequest, ProduceResponse, ServerConfigs, RecordingData, StatsInput, StatsOutput, StreamFileRequest, TransportBitrateData, TransportData, WorkerLoadData, ListData, StreamData, FilePathInput, PushStreamInputsRequest, PushStreamInputsResponse, PullStreamInputsRequest, PullStreamInputsResponse, RecordingRequest, StreamKindsData, KindsByFileInput, KindsData } from './client-interfaces';
import { TransportOptions } from 'mediasoup-client/lib/Transport';
import { IMediasoupApi } from '../../ms/i-mediasoup-api';
export declare class MediasoupRestApi implements IMediasoupApi {
    private readonly url;
    private readonly token;
    private readonly log;
    private readonly timeouts;
    constructor(url: string, token: string, log?: typeof console.log);
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
    setMaxIncomingBitrate(json: TransportBitrateData): Promise<void>;
    producersStats(json: StatsInput): Promise<StatsOutput>;
    consumersStats(json: StatsInput): Promise<StatsOutput>;
    transportStats(json: StatsInput): Promise<StatsOutput>;
    workerLoad(): Promise<WorkerLoadData>;
    numWorkers(): Promise<NumWorkersData>;
    pipeToRemoteProducer(json: PipeToRemoteProducerRequest): Promise<void>;
    pipeFromRemoteProducer(json: PipeFromRemoteProducerRequest): Promise<void>;
    startRecording(json: RecordingRequest): Promise<void>;
    stopRecording(json: RecordingData): Promise<void>;
    fileStreaming(json: StreamFileRequest): Promise<void>;
    stopFileStreaming(json: StreamKindsData): Promise<void>;
    recordedStreams(): Promise<ListData>;
    streamRecordings(json: StreamData): Promise<ListData>;
    deleteStreamRecordings(json: StreamData): Promise<void>;
    deleteRecording(json: FilePathInput): Promise<void>;
    pushToServerInputs(json: PushStreamInputsRequest): Promise<PushStreamInputsResponse>;
    pullFromServerInputs(json: PullStreamInputsRequest): Promise<PullStreamInputsResponse>;
    kindsByFile(json: KindsByFileInput): Promise<KindsData>;
    clear(): void;
    private request;
}
