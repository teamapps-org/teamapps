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
/// <reference types="node" />
import { EventEmitter } from "events";
import { MediaKind } from 'mediasoup-client/lib/RtpParameters';
import { ConferenceInput, ConsumerLayers } from './client-interfaces';
export declare interface ConferenceApi {
    on(event: 'bitRate', listener: ({ bitRate: number, kind: MediaKind }: {
        bitRate: any;
        kind: any;
    }) => void): this;
    on(event: 'connectionstatechange', listener: ({ state: string }: {
        state: any;
    }) => void): this;
    on(event: 'newTransportId', listener: ({ id: string }: {
        id: any;
    }) => void): this;
    on(event: 'newProducerId', listener: ({ id: string, kind: MediaKind }: {
        id: any;
        kind: any;
    }) => void): this;
    on(event: 'newConsumerId', listener: ({ id: string, kind: MediaKind }: {
        id: any;
        kind: any;
    }) => void): this;
    on(event: 'addtrack', listener: (event: MediaStreamTrackEvent) => void): this;
    on(event: 'removetrack', listener: (event: MediaStreamTrackEvent) => void): this;
}
export declare class ConferenceApi extends EventEmitter {
    private readonly api;
    private readonly configs;
    private readonly device;
    private readonly connectors;
    private readonly layers;
    private readonly log;
    private operation;
    private transport;
    private mediaStream?;
    private transportTimeout;
    private iceServers;
    private simulcast;
    private readonly timeouts;
    constructor(configs: ConferenceInput);
    setPreferredLayers(layers: ConsumerLayers): Promise<void>;
    addTrack(track: MediaStreamTrack): Promise<void>;
    removeTrack(track: MediaStreamTrack): Promise<void>;
    setMaxPublisherBitrate(bitrate: number): Promise<void>;
    updateKinds(kinds: MediaKind[]): Promise<void>;
    private init;
    publish(mediaStream: MediaStream): Promise<MediaStream>;
    subscribe(): Promise<MediaStream>;
    private subscribeTrack;
    private publishTrack;
    private consume;
    private listenStats;
    close(hard?: boolean): Promise<void>;
    private closeConnectors;
    private restartAll;
    private getTransport;
    private static originOptions;
}
